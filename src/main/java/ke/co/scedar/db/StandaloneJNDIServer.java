package ke.co.scedar.db;

import org.jnp.server.Main;
import org.jnp.server.NamingBeanImpl;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.net.InetAddress;
import java.util.Hashtable;
import java.util.concurrent.Callable;

public class StandaloneJNDIServer implements Callable<Object> {

    private String bindName;
    private Object bindObject;

    StandaloneJNDIServer(String bindName, Object bindObject){

        this.bindName = bindName;
        this.bindObject = bindObject;

        try {
            call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object call() throws Exception {

        //configure the initial factory
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");

        //start the naming info bean
        final NamingBeanImpl naming = new NamingBeanImpl();
        naming.start();

        //start the jnp serve
        final Main server = new Main();
        server.setNamingInfo(naming);
        server.setPort(5400);
        server.setBindAddress(InetAddress.getLocalHost().getHostName());
        server.start();

        //configure the environment for initial context
        final Hashtable<String, String> properties = new Hashtable<>();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
        properties.put(Context.PROVIDER_URL, "jnp://10.10.10.200:5400");

        //bind a name
        final Context context = new InitialContext(properties);
        context.bind(bindName, bindObject);

        return null;
    }
}