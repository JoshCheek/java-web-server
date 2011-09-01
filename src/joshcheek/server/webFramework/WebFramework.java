package joshcheek.server.webFramework;

import joshcheek.server.*;

import java.io.IOException;
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
    private HashMap<String,HashMap<String,AbstractRequest>> routes =
        new HashMap<String,HashMap<String,AbstractRequest>>();
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
        HashMap<String, AbstractRequest> methodsRoutes = routes.get(method);
        if(methodsRoutes == null)
            return false;
        return methodsRoutes.containsKey(uri);
    }

    private void register(AbstractRequest request) {
        ensureCanRespondTo(request);
        routes.get(request.method()).put(request.uri(), request);
    }

    private void ensureCanRespondTo(AbstractRequest request) {
        String method = request.method();
        if(routes.get(method) == null)
            routes.put(method, new HashMap<String, AbstractRequest>());
    }

    public void respondTo(String method, String uri, HTTPInteraction interaction) {
        requestFor(method, uri).respondTo(interaction);
    }

    private AbstractRequest requestFor(String method, String uri) {
        HashMap<String, AbstractRequest> methodsRoutes = routes.get(method);
        if(methodsRoutes == null) return new FourOhFourRequest();
        AbstractRequest request = methodsRoutes.get(uri);
        if(request == null) return new FourOhFourRequest();
        return request;
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
            interaction.writeResponse();
        }
    }


    public abstract class AbstractRequest {
        private String uri;
        private HTTPInteraction interaction;

        public abstract String controller();
        public abstract String method();

        public AbstractRequest(String uri) {
            this.uri = uri;
            register(this);
        }

        public AbstractRequest() {
            // non registering version
        }

        public String uri() {
            return uri;
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
