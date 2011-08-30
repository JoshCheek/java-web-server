package joshcheek.server;

import java.io.BufferedReader;
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

    private BufferedReader reader;
    private String requestMethod;
    private String requestURI;
    private String requestProtocolVersion;

    public void serve(Socket socket) throws IOException {
        setReader(socket);
        processHeader();
    }

    private void setReader(Socket socket) throws IOException {
        reader = SocketService.getBufferedReader(socket);
    }

    public String requestMethod() {
        return requestMethod;
    }

    public String requestURI() {
        return requestURI;
    }

    public String requestProtocolVersion() {
        return requestProtocolVersion;
    }

    private void processHeader() throws IOException {
        String[] firstLine = reader.readLine().split(" ");
        requestMethod           = firstLine[0];
        requestURI              = firstLine[1];
        requestProtocolVersion  = firstLine[2];
    }
}
