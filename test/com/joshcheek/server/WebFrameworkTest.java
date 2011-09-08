package com.joshcheek.server;

import com.joshcheek.server.webFramework.WebFramework;

import java.io.*;
import java.net.Socket;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 9/1/11
 * Time: 1:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class WebFrameworkTest extends junit.framework.TestCase  {
    private ByteArrayOutputStream output;

    public void testICanUseTheWebFrameworkLikeThis() throws Exception {
        WebFramework greetingApp = new WebFramework(8082) {
            public void defineRoutes() {
                new GetRequest("/index") {
                    public String controller() {
                        setStatus(418);
                        setHeader("Content-Type", "text/plain");
                        return "Hello, world!";
                    }
                };
            }
        };

        // ensure everything is set up properly
        assertEquals(8082, greetingApp.port());
        assertTrue(greetingApp.doesItRespondTo("GET", "/index"));
        assertFalse(greetingApp.doesItRespondTo("GET", "/foobar"));

        // can we run it?
        assertFalse(greetingApp.isRunning());
        greetingApp.startRunning();
        assertTrue(greetingApp.isRunning());
        greetingApp.stopRunning();
        assertFalse(greetingApp.isRunning());

        // do the requests work right?
        greetingApp.startRunning();
        assertResponds(greetingApp, "/index", 418, "Hello, world!", "Content-Type", "text/plain");
        assertResponds(greetingApp, "/foobar", 404);
        greetingApp.stopRunning();
    }

    public void testAWebFrameworkTakesItsPort() {
        WebFramework app = new WebFramework(1234) {
            public void defineRoutes() {}
        };
        assertEquals(1234, app.port());
    }

    public void testRespondsToNoRoutesByDefault() {
        WebFramework app = new WebFramework(1234) {
            public void defineRoutes() {}
        };
        assertFalse(app.doesItRespondTo("GET", "/"));
    }

    public void testCanDefineRoutesForGetRequests() {
        WebFramework app = new WebFramework(1234) {
            public void defineRoutes() {
                new GetRequest("/index") {
                    public String controller() {
                        return "";
                    }
                };
            }
        };
        assertTrue(  app.doesItRespondTo("GET"  , "/index"));
        assertFalse( app.doesItRespondTo("POST" , "/index"));
        assertFalse( app.doesItRespondTo("GET"  , "/foobar"));
    }

    public void testCanDefineRoutesForPostRequests() {
        WebFramework app = new WebFramework(1234) {
            public void defineRoutes() {
                new PostRequest("/index") {
                    public String controller() {
                        return "";
                    }
                };
            }
        };
        assertTrue(  app.doesItRespondTo("POST" , "/index"));
        assertFalse( app.doesItRespondTo("GET"  , "/index"));
        assertFalse( app.doesItRespondTo("POST" , "/foobar"));
    }

    public void testControllerCanSetTheStatusCode() {
        WebFramework app = new WebFramework(1234) {
            public void defineRoutes() {
                new PostRequest("/index") {
                    public String controller() {
                        setStatus(100);
                        return "Hello, world!";
                    }
                };
            }
        };
        assertTrue(100 == interactionFor(app, "POST", "/index").getStatus());
    }

    public void testStatusDefaultsTo200IfFoundAnd404IfNotFound() {
        WebFramework app = new WebFramework(1234) {
            public void defineRoutes() {
                new GetRequest("/index") {
                    public String controller() { return ""; }
                };
            }
        };
        assertTrue(200 == interactionFor(app, "GET",  "/index").getStatus());
        assertTrue(404 == interactionFor(app, "GET", "/foobar").getStatus());

    }

    public void testControllerCanSetTheHeaders() {
        WebFramework app = new WebFramework(1234) {
            public void defineRoutes() {
                new PostRequest("/index") {
                    public String controller() {
                        setHeader("Content-Type",   "text/plain");
                        setHeader("Accept-Charset", "utf-8");
                        return "Hello, world!";
                    }
                };
            }
        };
        HTTPInteraction interaction = interactionFor(app, "POST", "/index");
        assertEquals("text/plain", interaction.headerFor("Content-Type"));
        assertEquals("utf-8", interaction.headerFor("Accept-Charset"));
    }

    public void testControllerReturnValueIsTheContent() {
        WebFramework app = new WebFramework(1234) {
            public void defineRoutes() {
                new PostRequest("/index") {
                    public String controller() {
                        return "Hello, world!";
                    }
                };
            }
        };
        HTTPInteraction interaction = interactionFor(app, "POST", "/index");
        assertEquals("Hello, world!", interaction.getContent());
    }

    public void testNoContentIfControllerReturnsNull() {
        WebFramework app = new WebFramework(1234) {
            public void defineRoutes() {
                new PostRequest("/index") {
                    public String controller() {
                        return null;
                    }
                };
            }
        };
        HTTPInteraction interaction = interactionFor(app, "POST", "/index");
        assertEquals("", interaction.getContent());
        assertEquals("0", interaction.headerFor("Content-Length"));
    }

    public void testCanMatchUrl() {
        WebFramework app = new WebFramework(1234) {
            public void defineRoutes() {
                new PostRequest("/:route") {
                    public String controller() {
                        return getParam("route");
                    }
                };
            }
        };
        HTTPInteraction interaction = interactionFor(app, "POST", "/abcdefg");
        assertEquals("abcdefg", interaction.getContent());
    }






    public String output() {
        return output.toString();
    }

    private HTTPInteraction interactionFor(WebFramework app, String method, String uri) {
        HTTPInteraction interaction = mockHTTPInteraction(uri);
        app.respondTo(method, uri, interaction);
        return interaction;
    }

    private HTTPInteraction mockHTTPInteraction() {
        return mockHTTPInteraction("/");
    }

    private HTTPInteraction mockHTTPInteraction(String uri) {
        try {
            return new HTTPInteraction(mockReader(uri), mockWriter());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private BufferedReader mockReader(String uri) {
        return new BufferedReader(new StringReader("GET " + uri + " HTTP/1.1"));
    }

    private PrintStream mockWriter() {
        output = new ByteArrayOutputStream();
        return new PrintStream(output);
    }

    private void assertResponds(WebFramework app, String uri, int status, String content, String ... headers) throws Exception {
        Socket socket = getSocket(app);
        sendRequest(socket, uri);
        Thread.sleep(300);
        String response = getResponse(socket);
        assertHasStatus(response, status);
        assertHasContent(response, content);
        assertHasHeaders(response, headers);
        socket.close();
    }

    private void assertResponds(WebFramework app, String uri, int status) throws Exception {
        Socket socket = getSocket(app);
        sendRequest(socket, uri);
        Thread.sleep(300);
        String response = getResponse(socket);
        assertHasStatus(response, status);
        socket.close();
    }

    private void assertHasStatus(String response, int status) {
        String firstLine = response.substring(0, response.indexOf('\n'));
        assertMatches(Integer.toString(status), firstLine);
    }

    private void sendRequest(Socket socket, String uri) throws IOException {
        PrintStream writer = SocketService.getPrintStream(socket);
        writer.print("GET " + uri + " HTTP/1.1\r\n\r\n");
    }

    private Socket getSocket(WebFramework app) throws IOException {
        return new Socket("localhost", app.port());
    }

    private String getResponse(Socket socket) throws IOException {
        BufferedReader reader = SocketService.getBufferedReader(socket);
        String answer = "";
        for(int c; (c=reader.read()) != -1; )
            answer += Character.toString((char) c);
        return answer;
    }

    private void assertHasHeaders(String response, String[] headers) {
        for(int i=0; i<headers.length; i += 2)
            assertMatches(regexFor(headers[i], headers[i+1]), response);
    }

    private String regexFor(String key, String value) {
        return key + ":\\s+" + value + "\r\n";
    }

    private void assertHasContent(String response, String content) {
        assertTrue(response.endsWith(content));
    }

    private void assertMatches(String regex, String toMatch) {
        boolean doesMatch = Pattern.compile(".*" + regex + ".*", Pattern.DOTALL).matcher(toMatch).matches();
        assertTrue("Expected \"" + output + "\" to match /" + regex+"/", doesMatch);
    }
}
