import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class SpiderJetty {

    public static int JETTY_SERVER_PORT = 8080;

    private static ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    public static void doStart() throws Exception {

        Server server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(JETTY_SERVER_PORT);

        String webDefault = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "**/webdefault.xml";
        Resource web = resolver.getResources(webDefault)[0];
        String descriptor = web.getFile().getAbsolutePath();

        String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "**/" + SpiderJetty.class.getName() + ".class";
        Resource resource = resolver.getResources(pattern)[0];
        String resourcePath = resource.getFile().getAbsolutePath().replaceAll("target.*$", "") + "webapp";

        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setDefaultsDescriptor(descriptor);
        context.setResourceBase("file:" + resourcePath);
        context.setClassLoader(Thread.currentThread().getContextClassLoader());

        server.setConnectors(new Connector[]{connector});
        server.setHandler(context);

        server.setStopAtShutdown(true);
        server.setSendServerVersion(false);
        server.setSendDateHeader(false);
        server.setGracefulShutdown(1000);


        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        doStart();
    }
}
