package com.joshcheek.server.webFramework;

import com.joshcheek.server.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 9/1/11
 * Time: 1:47 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class WebFramework {
    private int port;
    private HashMap<String,ArrayList<AbstractRequest>> routes =
        new HashMap<String,ArrayList<AbstractRequest>>();
    private boolean running = false;
    private SocketService socketService;;

    public WebFramework(int port) {
        this.port = port;
        defineRoutes();
    }

    public abstract void defineRoutes();

    public int port() {
        return port;
    }

    public boolean doesItRespondTo(String method, String uri) {
        ArrayList<AbstractRequest> methodsRoutes = routes.get(method);
        if(methodsRoutes == null)
            return false;
        for(AbstractRequest request : methodsRoutes)
            if(request.doesItRespondTo(uri))
                return true;
        return false;
    }

    private void register(AbstractRequest request) {
        ensureCanRespondTo(request);
        routes.get(request.method()).add(request);
    }

    private void ensureCanRespondTo(AbstractRequest request) {
        String method = request.method();
        if(routes.get(method) == null)
            routes.put(method, new ArrayList<AbstractRequest>());
    }

    public void respondTo(String method, String uri, HTTPInteraction interaction) {
        requestFor(method, uri).respondTo(interaction);
    }

    private AbstractRequest requestFor(String method, String uri) {
        if(!doesItRespondTo(method, uri)) return new FourOhFourRequest();
        for(AbstractRequest request : routes.get(method))
            if(request.doesItRespondTo(uri))
                return request;
        return null; // shouldn't be possible because we checked that it responds to this uri
    }

    public boolean isRunning() {
        return running;
    }

    public void startRunning() throws IOException {
        if(isRunning()) return;
        socketService = new SocketService();
        socketService.serve(port(), getServer());
        running = true;
    }

    public void stopRunning() {
        if(!isRunning()) return;
        try { socketService.close(); } catch (Exception e) {}
        running = false;
    }

    public HTTPServer getServer() {
        return new HTTPServer(getHandlerFactory());
    }


    private HTTPRequestHandlerFactory getHandlerFactory() {
        final WebFramework that = this;
        return new HTTPRequestHandlerFactory() {
            public HTTPRequestHandler getHandler() throws IOException {
                return that.new RequestHandler();
            }
        };
    }

    private class RequestHandler implements HTTPRequestHandler {
        public void handle(HTTPInteraction interaction) throws IOException {
            AbstractRequest request = requestFor(interaction.requestMethod(), interaction.requestUri());
            request.respondTo(interaction);
        }
    }


    public abstract class AbstractRequest {
        private String uriPattern;
        private HTTPInteraction interaction;
        private ParamParser parser;

        public abstract String controller();
        public abstract String method();

        public AbstractRequest(String uriPattern) {
            this.uriPattern = uriPattern;
            this.parser = new ParamParser(uriPattern);
            register(this);
        }

        public AbstractRequest() {
            // non registering version
        }

        public String uri() {
            return uriPattern;
        }

        public void respondTo(HTTPInteraction interaction) {
            this.interaction = interaction;
            interaction.setContent(controller());
        }

        protected void setStatus(int code) {
            interaction.setStatus(code);
        }

        protected void setHeader(String key, Object value) {
            interaction.setHeader(key, value);
        }

        public String getParam(String name) {
            ParamParser parser = new ParamParser(uri());
            return parser.paramFor(interaction.requestUri(), name);
        }

        public boolean doesItRespondTo(String uri) {
            return parser.doesItMatch(uri);
        }
    }


    public abstract class GetRequest extends AbstractRequest {
        public GetRequest(String uri) {
            super(uri);
        }

        public String method() {
            return "GET";
        }
    }


    public abstract class PostRequest extends AbstractRequest {
        public PostRequest(String uri) {
            super(uri);
        }

        public String method() {
            return "POST";
        }
    }

    private class FourOhFourRequest extends AbstractRequest {
        public String controller() {
            setStatus(404);
            return null;
        }

        public String method() {
            return "NONE";
        }
    }
}
