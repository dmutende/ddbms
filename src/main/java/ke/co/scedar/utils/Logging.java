package ke.co.scedar.utils;

//import org.apache.logging.log4j.Level;
//import org.apache.logging.log4j.core.config.Configurator;
//import org.apache.logging.log4j.core.config.builder.api.*;
//import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

public class Logging {

//    public static void initialize(){
//        ConfigurationBuilder<BuiltConfiguration> builder
//                = ConfigurationBuilderFactory.newConfigurationBuilder();
//
//        //Standard logging layout
//        LayoutComponentBuilder standard
//                = builder.newLayout("PatternLayout");
//        standard.addAttribute("pattern", "%d [%t] %-5level: %msg%n%throwable");
//
//        //Logging trigger
//        ComponentBuilder triggeringPolicies = builder.newComponent("Policies")
//                .addComponent(builder.newComponent("CronTriggeringPolicy")
//                        .addAttribute("schedule", "0 0 0 * * ?"))
//                .addComponent(builder.newComponent("SizeBasedTriggeringPolicy")
//                        .addAttribute("size", "1M"));
//
//        //Set up Root logger
//        RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Level.DEBUG);
//        rootLogger.add(builder.newAppenderRef("stdout"));
//        builder.add(rootLogger);
//
//        //Console logger
//        AppenderComponentBuilder console = builder.newAppender("stdout", "Console");
//        console.add(standard);
//        builder.add(console);
//
//        //File logger
//        AppenderComponentBuilder file = builder.newAppender("log", "File");
//        file.addAttribute("fileName", "logs/ddbms.log");
//        file.add(standard);
//        builder.add(file);
//
//        AppenderComponentBuilder rollingFile
//                = builder.newAppender("rolling", "RollingFile");
////        rollingFile.addAttribute("fileName", "logs/rolling.log");
//        rollingFile.addAttribute("fileName", "logs/rolling.html");
////        rollingFile.addAttribute("filePattern", "rolling-%d{MM-dd-yy}.log.gz");
//        rollingFile.addAttribute("filePattern", "debug-backup-%d{MM-dd-yy-HH-mm-ss}-%i.html.gz");
//        rollingFile.addAttribute("type", "HTMLLayout");
//        rollingFile.addAttribute("charset", "UTF-8");
//        rollingFile.addAttribute("title", "Waah! Ok");
//        rollingFile.addAttribute("locationInfo", true);
//        rollingFile.addComponent(triggeringPolicies);
//        rollingFile.add(standard);
//        builder.add(rollingFile);
//
//        Configurator.initialize(builder.build());
//    }

    public static void log(){
        System.out.println();
    }

    public static void log(Object log){
        log(log, 0);
    }

    public static void log(Object log, long delay){

        try {
            Thread.sleep(delay);
            if(delay == 0) System.out.println(log);
            else out(log.toString()+"\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void out(String out){
        try {
            char[] chars = out.toCharArray();
            for (char aChar : chars) {
                System.out.print(aChar);
                Thread.sleep(0);
            }
        } catch (InterruptedException ignore) {}
    }
}
