package joshcheek.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 8/30/11
 * Time: 12:17 PM
 * To change this template use File | Settings | File Templates.
 */
public interface HTTPRequestHandler {
    public void   handle(BufferedReader reader, PrintStream writer) throws IOException;
}
