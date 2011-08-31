package joshcheek.server;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 8/31/11
 * Time: 7:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class HTTPRequestHandlerFactory {

    private static HTTPRequestHandler mockHandler = null;

    public static HTTPRequestHandler getHandler() {
        if(mockHandler == null)
            return new HTTPRequestHandlerImp();
        else {
            return mockHandler;
        }
    }

    public static void handleWith(HTTPRequestHandlerMock handler) {
        mockHandler = handler;
    }
}
