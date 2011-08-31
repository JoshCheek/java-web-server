package joshcheek.server;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 8/31/11
 * Time: 7:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class HTTPRequestHandlerMock implements HTTPRequestHandler {

    private String fullMessage = "";

    public void handle(BufferedReader reader) throws IOException {
        extractMessage(reader);
    }

    public String fullMessage() {
        return fullMessage;
    }

    private void extractMessage(BufferedReader reader) throws IOException {
        fullMessage = "";
        for( int c; (c=reader.read()) != -1 ; )
            fullMessage += Character.toString((char)c);
    }
}
