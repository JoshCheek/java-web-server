package joshcheek.server;

import joshcheek.server.webFramework.WebFramework;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 9/1/11
 * Time: 1:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class WebFrameworkTest extends junit.framework.TestCase  {
    private ByteArrayOutputStream output;

//    public void testICanUseTheWebFrameworkLikeThis() {
//        WebFramework greetingApp = new WebFramework(8081) {
//            public void defineRoutes() {
//                new GetRequest("/index") {
//                    public String controller() {
//                        setResponseCode(418);
//                        setHeader("Content-Type", "text/plain");
//                        return "Hello, world!";
//                    }
//                }
//            }
//        }
//
//        // ensure everything is set up properly
//        assertEquals(8081, greetingApp.port());
//        assertTrue(greetingApp.respondTo("/index"));
//        assertFalse(greetingApp.respondTo("/foobar"));
//
//        // can we run it?
//        assertFalse(greetingApp.isRunning());
//        greetingApp.startRunning();
//        assertTrue(greetingApp.isRunning());
//        greetingApp.stopRunning();
//        assertFalse(greetingApp.isRunning());
//
//        // do the requests work right?
//        assertResponds(greetingApp, "/index", 418, "Hello, world!", "Content-Type", "text/plain");
//        assertResponds(greetingApp, "/foobar", 404);
//    }

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


    public String output() {
        return output.toString();
    }

    private HTTPInteraction interactionFor(WebFramework app, String method, String uri) {
        HTTPInteraction interaction = mockHTTPInteraction();
        app.respondTo(method, uri, interaction);
        return interaction;
    }

    private HTTPInteraction mockHTTPInteraction() {
        try {
            return new HTTPInteraction(mockReader(), mockWriter());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private BufferedReader mockReader() {
        return new BufferedReader(new StringReader("GET / HTTP/1.1"));
    }

    private PrintStream mockWriter() {
        output = new ByteArrayOutputStream();
        return new PrintStream(output);
    }
}
