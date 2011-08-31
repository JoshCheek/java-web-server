package joshcheek.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 8/31/11
 * Time: 7:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class HTTPRequestHandlerMock implements HTTPRequestHandler {

    private String fullMessage = "";
    private String response = "";

    public void handle(BufferedReader reader, PrintStream writer) throws IOException {
        extractMessage(reader);
        writeMessage(writer);
    }

    public void respondWith(String response) {
        this.response = response;
    }

    public String fullMessage() {
        return fullMessage;
    }

    private void writeMessage(PrintStream writer) {
        writer.println(response);
        writer.flush();
    }

    private void extractMessage(BufferedReader reader) throws IOException {
        fullMessage = "";
        for( int c; !endOfMessage() && (c=reader.read()) != -1 ; )
            fullMessage += Character.toString((char)c);
    }

    private boolean endOfMessage() {
        return  fullMessage().length() >= 4 &&
                lastFourOfMessage().equals("\r\n\r\n");
    }

    private String lastFourOfMessage() {
        return fullMessage().substring(fullMessage().length() - 4);
    }
}
