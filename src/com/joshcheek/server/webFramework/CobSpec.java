package com.joshcheek.server.webFramework;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 9/1/11
 * Time: 7:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class CobSpec {
    public static void main(String[] args) throws IOException {


        WebFramework cobSpecApp = new WebFramework(8086) {
            public void defineRoutes() {
                new GetRequest("/") {
                    public String controller() {
                        return "Hello, world!";
                    }
                };

                new GetRequest("/SimultaneousRequests") {
                    public String controller() {
                        return "Simultaneous :)";
                    }
                };

                new GetRequest("/abc/:def/ghi") {
                    public String controller() {
                        return getParam("def");
                    }
                };

            }
        };


        cobSpecApp.startRunning();
    }

}
