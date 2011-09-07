package com.joshcheek.server;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 8/31/11
 * Time: 1:50 PM
 * To change this template use File | Settings | File Templates.
 */
public interface HTTPRequestHandlerFactory {
    HTTPRequestHandler getHandler() throws IOException;
}
