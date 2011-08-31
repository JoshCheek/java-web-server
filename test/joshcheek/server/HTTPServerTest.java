package joshcheek.server;

import java.io.BufferedReader;
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

    // primarily getting interface expectations from http://www.w3.org/Protocols/HTTP/HTTP2.html
    //                                           and http://jmarshall.com/easy/http/

    public  static final int    PORT = SocketServiceTest.PORT;
    private SocketService       ss;
    private HTTPServer          server;
    private static final String GET_REQUEST="GET / HTTP/1.1\r\n\r\n";
    private static final String POST_REQUEST =  "POST /path/script.cgi HTTP/1.0\r\n" +
                                                "From: frog@jmarshall.com\r\n" +
                                                "User-Agent: HTTPTool/1.0\r\n" +
                                                "Content-Type: application/x-www-form-urlencoded\r\n" +
                                                "Content-Length: 32\r\n" +
                                                "\r\n" +
                                                "home=Cosby&favorite+flavor=flies\r\n\r\n";
    private String response="";
    private static HTTPRequestHandlerImp handler=null;
    private static String stubbedContent="";

    public void setUp() throws Exception {
        setSocketService();
        setServer();
        serveServer();
    }

    public void tearDown() throws Exception {
        stopServingServer();
    }

    public void testForwardsGetRequests() throws Exception {
        connect(GET_REQUEST);
        assertContains("GET", handler.method());
    }

    public void testForwardsPostRequests() throws Exception {
        connect(POST_REQUEST);
        assertContains("POST", handler.method());
    }

    public void testHandlerCanRespond() throws Exception {
        connect(GET_REQUEST, "this is the response");
        assertContains("this is the response", response);
    }



    private void assertContains(String shouldSee, String fullString) {
        assertTrue(
                "Expected \"" + shouldSee + "\" to be in \"" + fullString + "\"",
                fullString.contains(shouldSee));
    }

    private void connect(String requestLine, String stubbedResponse) throws IOException, InterruptedException {
        stubbedContent = stubbedResponse;
        Socket socket = new Socket("localhost", PORT);
        writeToServer(socket, requestLine);
        recordResponse(socket);
        socket.close();
        Thread.sleep(300);
    }

    private void connect(String requestLine) throws Exception {
        connect(requestLine, "response");
    }

    private void stubResponse(String stubbedResponse) {
        handler.setContent(stubbedResponse);
    }

    private void recordResponse(Socket socket) throws IOException {
        response = "";
        BufferedReader reader = SocketService.getBufferedReader(socket);
        for(int c=0; (c=reader.read()) != -1; )
            response += Character.toString((char)c);
    }

    private void serveServer() throws Exception {
        ss.serve(PORT, server);
    }

    private void setServer() {
        server = new HTTPServer(new HTTPRequestHandlerFactory() {
            public HTTPRequestHandler getHandler() {
                HTTPServerTest.handler = new HTTPRequestHandlerImp();
                handler.setContent(stubbedContent);
                return handler;
            }
        });
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
