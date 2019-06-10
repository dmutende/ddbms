package ke.co.scedar.api.handlers.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import com.fasterxml.jackson.core.type.TypeReference;
import ke.co.scedar.utils.Constants;
import ke.co.scedar.utils.DateTime;
import ke.co.scedar.utils.JvmManager;

import java.io.*;
import java.util.Collections;
import java.util.List;

public class CustomHandler implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        printRequestInfo(exchange);
    }

    protected void send(HttpServerExchange exchange, String data, Integer status, String contentType){
        exchange.setStatusCode(status);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, contentType);
        exchange.getResponseSender().send(data);
    }

    protected void send(HttpServerExchange exchange, Object data, Integer status){
        exchange.setStatusCode(status);

        String contentType = determineAccept(exchange);
        ObjectMapper objectMapper = null;

        if(contentType.equals(Constants.applicationJson)){
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, Constants.applicationJson);
            try {
                objectMapper = getResponseObjectMapper(exchange);
                exchange.getResponseSender().send(objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(data));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }else {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, Constants.applicationXml);
            try {
                objectMapper = getResponseObjectMapper(exchange);
                exchange.getResponseSender().send(objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(data));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        JvmManager.gc(objectMapper);
    }

    public static String toJson(Object obj){
        return serialize(obj, Constants.applicationJson);
    }

    public static String toXml(Object obj){
        return serialize(obj, Constants.applicationXml);
    }

    public static String serialize(Object obj, String contentType){
        ObjectMapper objectMapper = getObjectMapper(contentType);
        try {
            String objStr = objectMapper.writeValueAsString(obj);
            JvmManager.gc(obj, objectMapper);
            return objStr;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    protected Object getBodyObject(HttpServerExchange exchange, Class clazz){
        String contentType = determineContentType(exchange);
        String body = getBody(exchange);

        ObjectMapper objectMapper = getObjectMapper(contentType);

        try {
            Object obj = objectMapper.readValue(body, clazz);
            JvmManager.gc(objectMapper);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            exchange.addQueryParam(Constants.MARSHALL_ERROR,e.getMessage());
            return null;
        }
    }

    protected static Object[] fromJson(String data, Class clazz){

        ObjectMapper objectMapper = getObjectMapper(Constants.applicationJson);

        try {
            Object obj = objectMapper.readValue(data, clazz);
            JvmManager.gc(objectMapper);
            return new Object[] {1, obj};
        } catch (Exception e) {
            e.printStackTrace();
            return new Object[] {-1, Constants.MARSHALL_ERROR,e.getMessage()};
        }
    }

    @SuppressWarnings("unchecked")
    public static Object getObject(String data, Class clazz, String contentType){
        ObjectMapper objectMapper = getObjectMapper(contentType);
        try {
            Object obj = objectMapper.readValue(data, clazz);
            JvmManager.gc(objectMapper);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List getObject(String data, TypeReference typeReference){
        ObjectMapper objectMapper = getObjectMapper(Constants.applicationJson);
        try {
            List obj = objectMapper.readValue(data, typeReference);
            JvmManager.gc(objectMapper);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String determineContentType(HttpServerExchange exchange){
        try{
            return determineAorCt(exchange.getRequestHeaders()
                    .get("Content-Type").getFirst());
        }catch (NullPointerException e){
            return determineAorCt(Constants.applicationJson);
        }
    }

    private static String determineAccept(HttpServerExchange exchange){
        try{
            return determineAorCt(exchange.getRequestHeaders()
                    .get("Accept").getFirst());
        }catch (NullPointerException e){
            return determineAorCt(Constants.applicationJson);
        }
    }

    private static String determineAorCt(String headerValue){
        switch (headerValue) {
            case Constants.applicationJson:
                return Constants.applicationJson;
            case Constants.applicationXml:
                return Constants.applicationXml;
            default:
                return Constants.applicationJson;
        }
    }

    public static ObjectMapper getRequestObjectMapper(HttpServerExchange exchange){
        return getObjectMapper(determineContentType(exchange));
    }

    private static ObjectMapper getResponseObjectMapper(HttpServerExchange exchange){
        return getObjectMapper(determineAccept(exchange));
    }

    private static ObjectMapper getObjectMapper(String contentType){
        if(contentType.equals(Constants.applicationXml)){
            JacksonXmlModule xmlModule = new JacksonXmlModule();
            xmlModule.setDefaultUseWrapper(false);
            return new XmlMapper(xmlModule);
        }else{
            return new ObjectMapper();
        }
    }

    protected void printRequestInfo(HttpServerExchange exchange){
        String userAgentHeader = "UNKNOWN";
        try{
            userAgentHeader = exchange.getRequestHeaders().get("User-Agent").getFirst();
        }catch (NullPointerException ignore){}
        String user = userAgentHeader.toLowerCase();

        String os = "UNKNOWN";
        String browser = "UNKNOWN";

        // Determine OS
        if (user.contains("windows")) os = "Windows";
        else if (user.contains("linux")) os = "Linux";
        else if(user.contains("mac")) os = "Mac";
        else if(user.contains("x11")) os = "Unix";
        else if(user.contains("android")) os = "Android";
        else if(user.contains("iphone")) os = "IPhone";

        // Determine Browser
        if (user.contains("msie"))
        {
            String substring= userAgentHeader.substring(userAgentHeader.indexOf("MSIE")).split(";")[0];
            browser=substring.split(" ")[0].replace("MSIE", "IE")+"-"+substring.split(" ")[1];
        } else if (user.contains("safari") && user.contains("version"))
        {
            browser=(userAgentHeader.substring(userAgentHeader.indexOf("Safari")).split(" ")[0]).split("/")[0]+"-"+(userAgentHeader.substring(userAgentHeader.indexOf("Version")).split(" ")[0]).split("/")[1];
        } else if ( user.contains("opr") || user.contains("opera"))
        {
            if(user.contains("opera"))
                browser=(userAgentHeader.substring(userAgentHeader.indexOf("Opera")).split(" ")[0]).split("/")[0]+"-"+(userAgentHeader.substring(userAgentHeader.indexOf("Version")).split(" ")[0]).split("/")[1];
            else if(user.contains("opr"))
                browser=((userAgentHeader.substring(userAgentHeader.indexOf("OPR")).split(" ")[0]).replace("/", "-")).replace("OPR", "Opera");
        } else if (user.contains("chrome"))
        {
            browser=(userAgentHeader.substring(userAgentHeader.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
        } else if ((user.contains("mozilla/7.0")) || (user.contains("netscape6"))  || (user.contains("mozilla/4.7")) || (user.contains("mozilla/4.78")) || (user.contains("mozilla/4.08")) || (user.contains("mozilla/3")) )
        {
            browser = "Netscape-?";

        } else if (user.contains("firefox"))
        {
            browser=(userAgentHeader.substring(userAgentHeader.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
        } else if(user.contains("rv"))
        {
            browser="IE-" + user.substring(user.indexOf("rv") + 3, user.indexOf(")"));
        }

        System.out.println("\n*********** REQUEST INFO ***********");
        System.out.println("Request URI: " + exchange.getRequestURI());
        System.out.println("Protocol: " + exchange.getProtocol());
        System.out.println("Request Method: " + exchange.getRequestMethod());
        try{
            System.out.println("Remote Address: " + exchange.getRequestHeaders().get("X-Real-IP").getFirst());
        }catch (NullPointerException e){
            System.out.println("Remote Address: "+exchange.getSourceAddress().getAddress().toString());
            exchange.getRequestHeaders().add(
                    new HttpString("X-Real-IP"), exchange.getSourceAddress().getAddress().toString());
        }
        System.out.println("User-Agent: " + userAgentHeader);
        System.out.println("Remote OS: " + os);
        System.out.println("Remote Browser: " + browser);
        System.out.println("Timestamp: " + DateTime.getCurrentDateTime());
        System.out.println("**************************************\n");

        exchange.getResponseHeaders().put(
                new HttpString("Access-Control-Allow-Origin"),
                "*");
        exchange.getResponseHeaders().put(
                new HttpString("Access-Control-Allow-Methods"),
                "POST, GET, OPTIONS, PUT, PATCH, DELETE");
        exchange.getResponseHeaders().put(
                new HttpString("Access-Control-Allow-Headers"),
                "Content-Type, Accept, Authorization");
    }

    private String getBody(HttpServerExchange exchange){
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();

        try {
            exchange.startBlocking();
            reader = new BufferedReader(new InputStreamReader(exchange.getInputStream()));

            String line;
            while((line = reader.readLine()) != null ) {
                builder.append( line );
            }
        } catch(IOException e) {
            e.printStackTrace( );
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch( IOException e ) {
                    e.printStackTrace();
                }
            }
            JvmManager.gc(reader, builder);
        }

        String body = builder.toString();
        JvmManager.gc(reader, builder);

        return body;
    }
}
