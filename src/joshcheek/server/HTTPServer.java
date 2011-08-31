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

    private HTTPRequestHandlerFactory dispensary;

    public HTTPServer(HTTPRequestHandlerFactory dispensary) {
        this.dispensary = dispensary;
    }

    public void serve(Socket socket) throws IOException {
        handler().handle(interactionFor(socket));
    }


    private HTTPInteraction interactionFor(Socket socket) throws IOException {
        return new HTTPInteraction(reader(socket), writer(socket));
    }

    private HTTPRequestHandler handler() {
        return dispensary.getHandler();
    }

    private PrintStream writer(Socket socket) throws IOException {
        return SocketService.getPrintStream(socket);
    }

    private BufferedReader reader(Socket socket) throws IOException {
        return SocketService.getBufferedReader(socket);
    }


}
