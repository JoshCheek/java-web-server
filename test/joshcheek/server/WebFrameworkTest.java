package joshcheek.server;

import joshcheek.server.webFramework.WebFramework;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 9/1/11
 * Time: 1:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class WebFrameworkTest extends junit.framework.TestCase  {

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
        assertFalse(app.respondTo("GET", "/"));
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
        assertTrue(  app.respondTo("GET"  , "/index"));
        assertFalse( app.respondTo("POST" , "/index"));
        assertFalse( app.respondTo("GET"  , "/foobar"));
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
        assertTrue(  app.respondTo("POST" , "/index"));
        assertFalse( app.respondTo("GET"  , "/index"));
        assertFalse( app.respondTo("POST" , "/foobar"));
    }
}
