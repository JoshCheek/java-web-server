package joshcheek.server;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 8/30/11
 * Time: 12:17 PM
 * To change this template use File | Settings | File Templates.
 */
public interface HTTPRequestHandler {
    public void handle(HTTPInteraction interaction) throws IOException;
}
