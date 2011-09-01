package joshcheek.server.webFramework;

import joshcheek.server.HTTPInteraction;

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
        if(methodsRoutes == null) return null;
        return methodsRoutes.get(uri);
    }


    public abstract class AbstractRequest {
        private String uri;
        private HTTPInteraction interaction;

        public AbstractRequest(String uri) {
            this.uri = uri;
            register(this);
        }
        public abstract String controller();

        public abstract String method();

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
}
