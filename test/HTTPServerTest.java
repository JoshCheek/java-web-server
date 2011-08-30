import joshcheek.server.HTTPServer;
import joshcheek.server.SocketService;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 8/30/11
 * Time: 9:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class HTTPServerTest extends junit.framework.TestCase {
    public  static final int    PORT = SocketServiceTest.PORT;
    private SocketService       ss;
    private HTTPServer          server;

    public void setUp() throws Exception {
        setSocketService();
        setServer();
        serveServer();
    }

    public void tearDown() throws Exception {
        stopServingServer();
    }

    // primarily getting interface expectations from http://www.w3.org/Protocols/HTTP/HTTP2.html

    public void testRecognizesSimpleGetRequests() throws Exception {
        connect("GET /");
        assertEquals("GET", server.requestMethod());
        assertEquals("/", server.requestURI());
    }

    private void connect(String requestLine) throws Exception {

    }

    private void serveServer() throws Exception {
        ss.serve(PORT, new HTTPServer());
    }

    private void setServer() {
        server = new HTTPServer();
    }

    private void setSocketService() {
        ss = new SocketService();
    }

    private void stopServingServer() throws Exception {
        ss.close();
    }

}
