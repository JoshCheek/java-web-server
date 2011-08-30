package joshcheek.server;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 8/29/11
 * Time: 11:47 AM
 * To change this template use File | Settings | File Templates.
 */

public interface SocketServer {
    public void serve(Socket s) throws IOException;
}
