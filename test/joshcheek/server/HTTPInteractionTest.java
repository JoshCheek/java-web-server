package joshcheek.server;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

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
        assertEquals("GET",         interaction.requestMethod());
        assertEquals("/",           interaction.requestUri());
        assertEquals("HTTP/1.1",    interaction.requestProtocolVersion());
    }

    public void testRecognizesPostRequest() throws Exception {
        handle(POST_REQUEST);
        assertEquals("POST",                interaction.requestMethod());
        assertEquals("/path/script.cgi",    interaction.requestUri());
        assertEquals("HTTP/1.0",            interaction.requestProtocolVersion());
    }

    public void testResponseDefaultsToHTTP1_1With200() throws IOException {
        handle(GET_REQUEST);
        interaction.writeResponse();
        assertEquals("HTTP/1.1 200 OK\r\n", firstLineOfResponse());
    }

    public void testHandlesUnknownStatusCodes() throws IOException {
        handle(GET_REQUEST);
        interaction.setStatus(104);
        interaction.writeResponse();
        assertEquals(104, interaction.getStatus());
        assertEquals("HTTP/1.1 104 \r\n", firstLineOfResponse());
    }

    public void testKnowsKnownStatusCodes() throws IOException {
        String[][] expectations = new String[][]{
                {"100", "Continue"},
                {"101", "Switching Protocols"},
                {"102", "Processing"},
                {"103", "Checkpoint"},
                {"122", "Request-URI too long"},
                {"200", "OK"},
                {"201", "Created"},
                {"202", "Accepted"},
                {"203", "Non-Authoritative Information"},
                {"204", "No Content"},
                {"205", "Reset Content"},
                {"206", "Partial Content"},
                {"207", "Multi-Status"},
                {"226", "IM Used"},
                {"300", "Multiple Choices"},
                {"301", "Moved Permanently"},
                {"302", "Found"},
                {"303", "See Other"},
                {"304", "Not Modified"},
                {"305", "Use Proxy"},
                {"306", "Switch Proxy"},
                {"307", "Temporary Redirect"},
                {"308", "Resume Incomplete"},
                {"400", "Bad Request"},
                {"401", "Unauthorized"},
                {"402", "Payment Required"},
                {"403", "Forbidden"},
                {"404", "Not Found"},
                {"405", "Method Not Allowed"},
                {"406", "Not Acceptable"},
                {"407", "Proxy Authentication Required"},
                {"408", "Request Timeout"},
                {"409", "Conflict"},
                {"410", "Gone"},
                {"411", "Length Required"},
                {"412", "Precondition Failed"},
                {"413", "Request Entity Too Large"},
                {"414", "Request-URI Too Long"},
                {"415", "Unsupported Media Type"},
                {"416", "Requested Range Not Satisfiable"},
                {"417", "Expectation Failed"},
                {"418", "I'm a teapot"},
                {"422", "Unprocessable Entity"},
                {"423", "Locked"},
                {"424", "Failed Dependency"},
                {"425", "Unordered Collection"},
                {"426", "Upgrade Required"},
                {"444", "No Response"},
                {"449", "Retry With"},
                {"450", "Blocked by Windows Parental Controls"},
                {"499", "Client Closed Request"},
                {"500", "Internal Server Error"},
                {"501", "Not Implemented"},
                {"502", "Bad Gateway"},
                {"503", "Service Unavailable"},
                {"504", "Gateway Timeout"},
                {"505", "HTTP Version Not Supported"},
                {"506", "Variant Also Negotiates"},
                {"507", "Insufficient Storage"},
                {"509", "Bandwidth Limit Exceeded"},
                {"510", "Not Extended"},
        };
        for(String[] expectation : expectations) {
            int status = Integer.parseInt(expectation[0]);
            String reasonPhrase = expectation[1];
            handle(GET_REQUEST);
            interaction.setStatus(status);
            interaction.writeResponse();
            String expectedLine = "HTTP/1.1 " + status + " " + reasonPhrase + "\r\n";
            assertEquals(expectedLine, firstLineOfResponse());
        }
    }

    public void testCanSetArbitraryHeaders() throws IOException {
        handle(GET_REQUEST);
        interaction.setHeader("abc", "def");
        interaction.writeResponse();
        assertMatches("abc:\\s+def\r\n", output());
    }

    public void testCanOverrideHeaders() throws IOException {
        handle(GET_REQUEST);
        interaction.setHeader("abc", "def");
        interaction.setHeader("abc", "ghi");
        interaction.writeResponse();
        assertMatches("abc:\\s+ghi\r\n", output());
        assertDoesntMatch("abc:\\s+def\r\n", output());
    }

    public void testCanSetMultipleHeaders() throws IOException {
        handle(GET_REQUEST);
        interaction.setHeader("Allow", "GET HEAD PUT");
        interaction.setHeader("Content-Length", 20);
        interaction.writeResponse();
        assertEquals("GET HEAD PUT", interaction.headerFor("Allow"));
        assertMatches("Allow:\\s+GET HEAD PUT\r\n", output());
        assertMatches("Content-Length:\\s+20\r\n", output());
    }

    public void testCanSetContent() throws IOException {
        handle(GET_REQUEST);
        interaction.setContent("abcd");
        assertEquals("abcd", interaction.getContent());
        interaction.writeResponse();
        assertMatches("abcd", output());
    }

    public void testSettingContentSetsTheContentLength() throws IOException {
        handle(GET_REQUEST);
        interaction.setContent("abcd");
        interaction.writeResponse();
        assertMatches("Content-Length:\\s+4\r\n", output());
    }

    public void testContentTypeDefaultsToTextHtmlWithCharsetUtf8() throws IOException {
        assertDefaultHeader("Content-Type", "text/html;charset=utf-8");
    }

    public void testContentLengthDefaultsToZero() throws IOException {
        assertDefaultHeader("Content-Length", "0");
    }

    public void testServerDefaultsToJoshServer() throws IOException {
        assertDefaultHeader("Server", "JoshServer");
    }

    public void testDateDefaultsToToday() throws IOException {
        String              dateFormat  = "E, dd MMM yyyy HH:mm:ss z";
        SimpleDateFormat    formatter   = new SimpleDateFormat(dateFormat);
        Calendar            calendar    = Calendar.getInstance();
        String              today       = formatter.format(calendar.getTime());
        calendar.set(2011, Calendar.SEPTEMBER, 1, 15, 42, 47);
        String              septFirst   = formatter.format(calendar.getTime());
        assertEquals("Thu, 01 Sep 2011 15:42:47 CDT", septFirst);
        assertDefaultHeader("Date", today);
    }



    private void assertDefaultHeader(String key, String value) throws IOException {
        handle(GET_REQUEST);
        interaction.writeResponse();
        assertMatches(key + ":\\s+" + value + "\r\n", output());
    }

    private String output() {
        return output.toString();
    }

    private void assertMatches(String regex, String toMatch) {
        boolean doesMatch = Pattern.compile(".*"+regex+".*", Pattern.DOTALL).matcher(toMatch).matches();
        assertTrue("Expected \"" + output + "\" to match /" + regex+"/", doesMatch);
    }

    private void assertDoesntMatch(String regex, String toMatch) {
        boolean doesMatch = Pattern.compile(".*" + regex + ".*", Pattern.DOTALL).matcher(toMatch).matches();
        assertFalse("Expected \"" + output + "\" to NOT match /" + regex + "/", doesMatch);
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
