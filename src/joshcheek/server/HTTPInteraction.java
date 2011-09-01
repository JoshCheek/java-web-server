package joshcheek.server;

import javax.net.ssl.SSLEngineResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 8/31/11
 * Time: 1:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class HTTPInteraction {

    private static final String SP = " ";
    private static final String CRLF = "\r\n";
    private RequestProcessor requestProcessor;
    private ResponseProcessor responseProcessor;

    private static HashMap<Integer, String> knownCodes = new HashMap<Integer, String>();
    static {
        knownCodes.put(100, "Continue");
        knownCodes.put(101, "Switching Protocols");
        knownCodes.put(102, "Processing");
        knownCodes.put(103, "Checkpoint");
        knownCodes.put(122, "Request-URI too long");
        knownCodes.put(200, "OK");
        knownCodes.put(201, "Created");
        knownCodes.put(202, "Accepted");
        knownCodes.put(203, "Non-Authoritative Information");
        knownCodes.put(204, "No Content");
        knownCodes.put(205, "Reset Content");
        knownCodes.put(206, "Partial Content");
        knownCodes.put(207, "Multi-Status");
        knownCodes.put(226, "IM Used");
        knownCodes.put(300, "Multiple Choices");
        knownCodes.put(301, "Moved Permanently");
        knownCodes.put(302, "Found");
        knownCodes.put(303, "See Other");
        knownCodes.put(304, "Not Modified");
        knownCodes.put(305, "Use Proxy");
        knownCodes.put(306, "Switch Proxy");
        knownCodes.put(307, "Temporary Redirect");
        knownCodes.put(308, "Resume Incomplete");
        knownCodes.put(400, "Bad Request");
        knownCodes.put(401, "Unauthorized");
        knownCodes.put(402, "Payment Required");
        knownCodes.put(403, "Forbidden");
        knownCodes.put(404, "Not Found");
        knownCodes.put(405, "Method Not Allowed");
        knownCodes.put(406, "Not Acceptable");
        knownCodes.put(407, "Proxy Authentication Required");
        knownCodes.put(408, "Request Timeout");
        knownCodes.put(409, "Conflict");
        knownCodes.put(410, "Gone");
        knownCodes.put(411, "Length Required");
        knownCodes.put(412, "Precondition Failed");
        knownCodes.put(413, "Request Entity Too Large");
        knownCodes.put(414, "Request-URI Too Long");
        knownCodes.put(415, "Unsupported Media Type");
        knownCodes.put(416, "Requested Range Not Satisfiable");
        knownCodes.put(417, "Expectation Failed");
        knownCodes.put(418, "I'm a teapot");
        knownCodes.put(422, "Unprocessable Entity");
        knownCodes.put(423, "Locked");
        knownCodes.put(424, "Failed Dependency");
        knownCodes.put(425, "Unordered Collection");
        knownCodes.put(426, "Upgrade Required");
        knownCodes.put(444, "No Response");
        knownCodes.put(449, "Retry With");
        knownCodes.put(450, "Blocked by Windows Parental Controls");
        knownCodes.put(499, "Client Closed Request");
        knownCodes.put(500, "Internal Server Error");
        knownCodes.put(501, "Not Implemented");
        knownCodes.put(502, "Bad Gateway");
        knownCodes.put(503, "Service Unavailable");
        knownCodes.put(504, "Gateway Timeout");
        knownCodes.put(505, "HTTP Version Not Supported");
        knownCodes.put(506, "Variant Also Negotiates");
        knownCodes.put(507, "Insufficient Storage");
        knownCodes.put(509, "Bandwidth Limit Exceeded");
        knownCodes.put(510, "Not Extended");
    }

    public HTTPInteraction(BufferedReader reader, PrintStream writer) throws IOException {
        requestProcessor = new RequestProcessor(reader);
        responseProcessor = new ResponseProcessor(writer);
    }

    public String requestMethod() {
        return requestProcessor.method();
    }

    public String requestUri() {
        return requestProcessor.uri();
    }

    public String requestProtocolVersion() {
        return requestProcessor.protocolVersion();
    }

    public void setContent(String content) {
        responseProcessor.setContent(content);
    }

    public void writeResponse() {
        responseProcessor.writeResponse();
    }

    public void setHeader(String key, Object value) {
        responseProcessor.setHeader(key, value);
    }

    public void setStatus(int code) {
        responseProcessor.setStatus(code);
    }

    public int getStatus() {
        return responseProcessor.statusCode();
    }

    public String headerFor(String key) {
        return responseProcessor.headerFor(key);
    }

    public String getContent() {
        return responseProcessor.getContent();
    }


    public class ResponseProcessor {
        private HashMap<String, String> headers = new HashMap<String, String>();
        private int                     status  = 200;
        private String                  content = "";
        private PrintStream             writer;

        public ResponseProcessor(PrintStream writer) {
            this.writer = writer;
            setDefaultHeaders();
        }

        private void setDefaultHeaders() {
            setHeader("Content-Type", "text/html;charset=utf-8");
            setHeader("Content-Length", 0);
            setHeader("Server", "JoshServer");
            setHeader("Date", todaysDate());
        }

        public void setContent(String content) {
            if(content == null) content = "";
            setHeader("Content-Length", content.length());
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void writeResponse() {
            writer.print(response());
        }

        public String response() {
            return statusLine() + headers() + content;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        // value must implement toString
        public void setHeader(String key, Object value) {
            headers.put(key, value.toString());
        }

        private String headers() {
            String toReturn = "";
            for (String key : headers.keySet())
                toReturn += key + ": " + headers.get(key) + CRLF;
            return toReturn;
        }

        private String statusLine() {
            return httpVersion() + SP + statusCode() + SP + reasonPhrase() + CRLF;
        }

        private String reasonPhrase() {
            return statusMessageFor(statusCode());
        }

        private String statusMessageFor(int code) {
            if (knownCodes.containsKey(code))
                return knownCodes.get(code);
            else
                return "";
        }

        private int statusCode() {
            return status;
        }

        private String httpVersion() {
            return "HTTP/1.1";
        }

        private String todaysDate() {
            SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
            Date now = Calendar.getInstance().getTime();
            return formatter.format(now);
        }

        public String headerFor(String key) {
            return headers.get(key);
        }
    }


    public class RequestProcessor {
        private String requestMethod;
        private String requestURI;
        private String requestProtocolVersion;

        public RequestProcessor(BufferedReader reader) throws IOException {
            processHeader(reader);
        }

        public String method() {
            return requestMethod;
        }

        public String uri() {
            return requestURI;
        }

        public String protocolVersion() {
            return requestProtocolVersion;
        }

        private void processHeader(BufferedReader reader) throws IOException {
            String[] firstLine = reader.readLine().split(" ");
            requestMethod = firstLine[0];
            requestURI = firstLine[1];
            requestProtocolVersion = firstLine[2];
        }
    }

}
