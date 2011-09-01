package joshcheek.server.webFramework;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 9/1/11
 * Time: 1:47 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class WebFramework {
    private int port;

    public WebFramework(int port) {
        this.port = port;
    }

    public abstract void defineRoutes();

    public int port() {
        return port;
    }

    public boolean respondTo(String method, String uri) {
        return false;
    }
}
