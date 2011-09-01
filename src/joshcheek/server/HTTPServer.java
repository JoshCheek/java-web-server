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

    private HTTPRequestHandlerFactory factory;

    public HTTPServer(HTTPRequestHandlerFactory dispensary) {
        this.factory = dispensary;
    }

    public void serve(Socket socket) throws IOException {
        HTTPInteraction interaction = interactionFor(socket);
        handler().handle(interaction);
        interaction.writeResponse();
    }


    private HTTPInteraction interactionFor(Socket socket) throws IOException {
        return new HTTPInteraction(reader(socket), writer(socket));
    }

    private HTTPRequestHandler handler() throws IOException {
        return factory.getHandler();
    }

    private PrintStream writer(Socket socket) throws IOException {
        return SocketService.getPrintStream(socket);
    }

    private BufferedReader reader(Socket socket) throws IOException {
        return SocketService.getBufferedReader(socket);
    }


}
