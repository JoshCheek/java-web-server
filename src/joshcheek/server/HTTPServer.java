package joshcheek.server;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 8/30/11
 * Time: 9:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class HTTPServer implements SocketServer {
    public void serve(Socket s) throws IOException {

    }

    public String requestMethod() {
        return "GET";
    }

    public String requestURI() {
        return "/";
    }

    public String requestProtocolVersion() {
        return "HTTP/1.0";
    }
}
