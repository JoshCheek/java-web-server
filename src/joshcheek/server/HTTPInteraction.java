package joshcheek.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 8/31/11
 * Time: 1:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class HTTPInteraction {

    private String requestMethod;
    private String requestURI;
    private String requestProtocolVersion;
    private String content;
    private PrintStream writer = null;
    private BufferedReader reader = null;

    public HTTPInteraction(BufferedReader reader, PrintStream writer) throws IOException {
        this.reader = reader;
        this.writer = writer;
        processHeader();
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

    private void processHeader() throws IOException {
        String[] firstLine = reader.readLine().split(" ");
        requestMethod           = firstLine[0];
        requestURI              = firstLine[1];
        requestProtocolVersion  = firstLine[2];
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void writeResponse() {
        writer.print(content);
    }
}
