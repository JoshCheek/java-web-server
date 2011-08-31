package joshcheek.server;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 8/31/11
 * Time: 7:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class HTTPInteractionTest extends junit.framework.TestCase {
    private HTTPInteraction interaction;
    private static final String GET_REQUEST  =  "GET / HTTP/1.1\r\n\r\n";
    private static final String POST_REQUEST =  "POST /path/script.cgi HTTP/1.0\r\n" +
                                                "From: frog@jmarshall.com\r\n" +
                                                "User-Agent: HTTPTool/1.0\r\n" +
                                                "Content-Type: application/x-www-form-urlencoded\r\n" +
                                                "Content-Length: 32\r\n" +
                                                "\r\n" +
                                                "home=Cosby&favorite+flavor=flies\r\n\r\n";
    private ByteArrayOutputStream output =null;

    public void testRecognizesGetRequests() throws Exception {
        handle(GET_REQUEST);
        assertEquals("GET",         interaction.method());
        assertEquals("/",           interaction.uri());
        assertEquals("HTTP/1.1",    interaction.protocolVersion());
    }

    public void testRecognizesPostRequest() throws Exception {
        handle(POST_REQUEST);
        assertEquals("POST",                interaction.method());
        assertEquals("/path/script.cgi",    interaction.uri());
        assertEquals("HTTP/1.0",            interaction.protocolVersion());
    }

    public void testResponseDefaultsToHTTP1_1With200() throws IOException {
        handle(GET_REQUEST);
        interaction.writeResponse();
        assertEquals("HTTP/1.1 200 \r\n", firstLineOfResponse());
    }



    private String firstLineOfResponse() {
        String written = output.toString();
        return written.substring(0, written.indexOf("\n")+1);
    }

    private void handle(String request) throws IOException {
        interaction = new HTTPInteraction(mockReader(request), mockWriter());
    }

    private BufferedReader mockReader(String request) {
        return new BufferedReader(new StringReader(request));
    }

    private PrintStream mockWriter() {
        output = new ByteArrayOutputStream();
        return new PrintStream(output);
    }

}
