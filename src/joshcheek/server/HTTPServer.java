package joshcheek.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 8/30/11
 * Time: 9:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class HTTPServer implements SocketServer {

    public void serve(Socket socket) throws IOException {
        handler().handle(reader(socket), writer(socket));
    }

    private PrintStream writer(Socket socket) throws IOException {
        return SocketService.getPrintStream(socket);
    }

    private BufferedReader reader(Socket socket) throws IOException {
        return SocketService.getBufferedReader(socket);
    }

    private HTTPRequestHandler handler() {
        return HTTPRequestHandlerFactory.getHandler();
    }

}
