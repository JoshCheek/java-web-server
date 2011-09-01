package joshcheek.server;

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
}
