import java.io.*;
import java.net.Socket;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 8/29/11
 * Time: 5:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class HTTPFileServerTest extends junit.framework.TestCase {
    public  static final int    PORT = SocketServiceTest.PORT;
    private static final String TEST_FILE_NAME = "testfile.html";
    private static final String TEST_FILE_CONTENTS = "<html><body><p>Hello, World!</p></body></html>";
    private SocketService       ss;

    public void setUp() throws Exception {
        ss = new SocketService();
    }

    public void tearDown() throws Exception {
        ss.close();
    }

    public void testServesFile() throws Exception {
        String response = serveTestFileAndReturnResponse();
        assertContains(response, TEST_FILE_CONTENTS);
    }

    private String serveTestFileAndReturnResponse() throws Exception {
        createTestFile(TEST_FILE_NAME);
        serveTestFile();
        String response = readFromHTTPFileServer();
        deleteTestFile(TEST_FILE_NAME);
        return response;
    }

    private String readFromHTTPFileServer() throws IOException {
        Socket socket = new Socket("localhost", PORT);
        BufferedReader br = SocketService.getBufferedReader(socket);
        String response = readResponse(br);
        socket.close();
        return response;
    }

    private String readResponse(BufferedReader br) throws IOException {
        String response = "";
        int c=0;
        while( (c = br.read()) != -1 )
            response += Character.toString((char)c);
        return response;
    }

    private void serveTestFile() throws Exception {
        ss.serve(PORT, new HTTPFileServer(TEST_FILE_NAME));
    }

    private void assertContains(String container, String containee) {
        assertTrue("Expected \"" + container + "\" to contain \"" + containee + "\"", container.contains(containee));
    }

    private void createTestFile(String testFileName) {
        try {
            File file = new File(testFileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(TEST_FILE_CONTENTS);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteTestFile(String testFileName) {
        new File(testFileName).delete();
    }

    private class HTTPFileServer implements SocketServer {
        private String fileName;

        public HTTPFileServer(String testFileName) {
            fileName = testFileName;
        }

        public void serve(Socket s) {
            try {
                FileInputStream fstream = new FileInputStream(fileName);
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                PrintStream writer = SocketService.getPrintStream(s);
                int c;
                while ((c = reader.read()) != -1)
                    writer.write(c);
            } catch (IOException e) {
            }
        }
    }
}
