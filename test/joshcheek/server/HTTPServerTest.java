package joshcheek.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

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
    private static final String GET_REQUEST="GET / HTTP/1.1\r\n";
    private static final String POST_REQUEST =  "POST /path/script.cgi HTTP/1.0\r\n" +
                                                "From: frog@jmarshall.com\r\n" +
                                                "User-Agent: HTTPTool/1.0\r\n" +
                                                "Content-Type: application/x-www-form-urlencoded\r\n" +
                                                "Content-Length: 32\r\n" +
                                                "\r\n" +
                                                "home=Cosby&favorite+flavor=flies\r\n";
    private HTTPRequestHandlerMock request;

    public void setUp() throws Exception {
        setTestMode();
        setSocketService();
        setServer();
        serveServer();
    }

    private void setTestMode() {
        request = new HTTPRequestHandlerMock();
        HTTPRequestHandlerFactory.handleWith(request);
    }

    public void tearDown() throws Exception {
        stopServingServer();
    }

    // primarily getting interface expectations from http://www.w3.org/Protocols/HTTP/HTTP2.html
    //                                           and http://jmarshall.com/easy/http/

    public void testRecognizesGetRequests() throws Exception {
        connect(GET_REQUEST);
        assertEquals(GET_REQUEST, request.fullMessage());
    }

    public void testRecognizesPostRequest() throws Exception {
        connect(POST_REQUEST);
        assertEquals(POST_REQUEST, request.fullMessage());
    }

    private void connect(String requestLine) throws Exception {
        Socket socket = new Socket("localhost", PORT);
        writeToServer(socket, requestLine);
        socket.close();
        Thread.sleep(600);
    }

    private void serveServer() throws Exception {
        ss.serve(PORT, server);
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

    private void writeToServer(Socket socket, String toWrite) throws IOException {
        PrintStream out = SocketService.getPrintStream(socket);
        out.write(toWrite.getBytes());
        out.flush();
    }
}
