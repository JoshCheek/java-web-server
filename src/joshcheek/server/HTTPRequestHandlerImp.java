package joshcheek.server;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 8/31/11
 * Time: 7:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class HTTPRequestHandlerImp implements HTTPRequestHandler {

    private HTTPInteraction interaction;
    private String content = "";

    public void handle(HTTPInteraction interaction) throws IOException {
        this.interaction = interaction;
        setContent();
    }

    public String method() {
        return interaction.requestMethod();
    }

    public String uri() {
        return interaction.requestUri();
    }

    public String protocolVersion() {
        return interaction.requestProtocolVersion();
    }

    public void setContent(String content) {
        this.content = content;
    }

    private void setContent() {
        interaction.setContent(content);
    }
}
