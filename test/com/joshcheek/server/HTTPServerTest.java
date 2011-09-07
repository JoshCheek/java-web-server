package com.joshcheek.server;

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
    private static final String REQUEST="GET / HTTP/1.1\r\n\r\n";

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

    public void testForwardsRequestsToHandler() throws Exception {
        connect(REQUEST);
        assertEquals("GET", handler.method());
    }

    public void testHandlersResponseIsReturned() throws Exception {
        connect(REQUEST, "this is the response");
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

    public class HTTPRequestHandlerImp implements HTTPRequestHandler {

        private HTTPInteraction interaction;
        private String content = "";

        public void handle(HTTPInteraction interaction) throws IOException {
            this.interaction = interaction;
            setContent();
        }

        public String method() {
            return interaction.requestMethod();
        }

        public String uri() {
            return interaction.requestUri();
        }

        public String protocolVersion() {
            return interaction.requestProtocolVersion();
        }

        public void setContent(String content) {
            this.content = content;
        }

        private void setContent() {
            interaction.setContent(content);
        }
    }

}
