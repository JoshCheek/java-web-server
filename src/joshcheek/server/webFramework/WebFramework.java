package joshcheek.server.webFramework;

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
    private HashMap<String,HashMap<String,GetRequest>> routes =
        new HashMap<String,HashMap<String,GetRequest>>();

    public WebFramework(int port) {
        this.port = port;
        defineRoutes();
    }

    public abstract void defineRoutes();

    public int port() {
        return port;
    }

    public boolean respondTo(String method, String uri) {
        HashMap<String, GetRequest> methodsRoutes = routes.get(method);
        if(methodsRoutes == null)
            return false;
        return methodsRoutes.containsKey(uri);
    }

    public abstract class GetRequest {
        private String uri;

        public GetRequest(String uri) {
            this.uri = uri;
            register(this);
        }
        public abstract String controller();

        public String method() {
            return "GET";
        }

        public String uri() {
            return uri;
        }
    }

    private void register(GetRequest getRequest) {
        ensureCanRespondTo(getRequest);
        routes.get(getRequest.method()).put(getRequest.uri(), getRequest);
    }

    private void ensureCanRespondTo(GetRequest getRequest) {
        String method = getRequest.method();
        if(routes.get(method) == null)
            routes.put(method, new HashMap<String, GetRequest>());
    }
}
