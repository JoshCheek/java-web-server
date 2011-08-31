package joshcheek.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 8/31/11
 * Time: 7:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class HTTPRequestHandlerImp implements HTTPRequestHandler {

    private String requestMethod;
    private String requestURI;
    private String requestProtocolVersion;

    public void handle(BufferedReader reader, PrintStream writer) throws IOException {
        processHeader(reader);
    }

    public void handle(String request, PrintStream writer) throws IOException {
        StringReader stringReader = new StringReader(request);
        BufferedReader reader = new BufferedReader(stringReader);
        handle(reader, writer);
    }

    public String method() {
        return requestMethod;
    }

    public String uri() {
        return requestURI;
    }

    public String protocolVersion() {
        return requestProtocolVersion;
    }

    private void processHeader(BufferedReader reader) throws IOException {
        String[] firstLine = reader.readLine().split(" ");
        requestMethod           = firstLine[0];
        requestURI              = firstLine[1];
        requestProtocolVersion  = firstLine[2];
    }

}
