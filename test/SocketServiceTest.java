import java.io.*;
import java.net.Socket;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 8/29/11
 * Time: 10:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class SocketServiceTest extends junit.framework.TestCase {
    private static final int    PORT = 1501;
    private static final String TEST_FILE_NAME = "testfile.html";
    private static final String TEST_FILE_CONTENTS = "<html><body><p>Hello, World!</p></body></html>";
    private static int          threadsActive = 0;
    private int                 connections = 0;
    private SocketServer        connectionCounter;
    private SocketService       ss;


    public SocketServiceTest(String name) {
        super(name);
        connectionCounter = new SocketServer() {
            public void serve(Socket s) {
                 ++connections;
            }
        };
    }

    public void setUp() throws Exception {
        ss = new SocketService();
    }

    public void tearDown() throws Exception {
        ss.close();
    }

    public void testOneConnection() throws Exception {
        ss.serve(PORT, connectionCounter);
        connect(PORT);
        assertEquals(1, connections);
    }

    public void testManyConnections() throws Exception {
        ss.serve(PORT, connectionCounter);
        for( int i = 0; i < 10; ++i )
            connect(PORT);
        assertEquals(10, connections);
    }

    public void testSendMessage() throws Exception {
        ss.serve(PORT, new HelloServer());
        Socket              s       = new Socket("localhost", PORT);
        BufferedReader      br      = SocketService.getBufferedReader(s);
        String              answer  = br.readLine();
        assertEquals("Hello", answer);
    }

    public void testReceiveMessage() throws Exception {
        ss.serve(PORT, new EchoServer());
        Socket              s       = new Socket("localhost", PORT);
        BufferedReader      br      = SocketService.getBufferedReader(s);
        PrintStream         ps      = SocketService.getPrintStream(s);
        ps.println("MyMessage");
        String              answer  = br.readLine();
        s.close();
        assertEquals("MyMessage", answer);
    }

    public void testMultiThreaded() throws Exception {
        ss.serve(PORT, new EchoServer());
        Socket          s1  = new Socket("localhost", PORT);
        BufferedReader  br  = SocketService.getBufferedReader(s1);
        PrintStream     ps  = SocketService.getPrintStream(s1);

        Socket          s2  = new Socket("localhost", PORT);
        BufferedReader  br2 = SocketService.getBufferedReader(s2);
        PrintStream     ps2 = SocketService.getPrintStream(s2);

        ps2.println("MyMessage");
        String answer2 = br2.readLine();
        s2.close();

        ps.println("MyMessage");
        String answer = br.readLine();
        s1.close();

        assertEquals("MyMessage", answer2);
        assertEquals("MyMessage", answer);
    }

    public void testAllServersClosed() throws Exception {
        ss.serve(PORT, new WaitThenClose());
        Socket s1 = new Socket("localhost", PORT);
        Thread.sleep(20);
        assertEquals(1, threadsActive);
        ss.close();
        assertEquals(0, threadsActive);
    }

    public void testServesFile() throws Exception {
        createTestFile(TEST_FILE_NAME);
        ss.serve(PORT, new HTTPFileServer(TEST_FILE_NAME));
        Socket socket = new Socket("localhost", PORT);
        BufferedReader br = SocketService.getBufferedReader(socket);
        String response = "";
        int c=0;
        while( (c = br.read()) != -1 )
            response += Character.toString((char)c);
        socket.close();
        deleteTestFile(TEST_FILE_NAME);
        assertEquals(TEST_FILE_CONTENTS, response);
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

    private void connect(int port) {
        try {
            Socket s = new Socket("localhost", port);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            s.close();
        } catch (IOException e) {
            fail("could not connect");
        }
    }

    private class HelloServer implements SocketServer {

        public void serve(Socket s) {
            try {
                PrintStream ps = new PrintStream(s.getOutputStream());
                ps.println("Hello");
            } catch (IOException e) {
            }
        }
    }

    private class EchoServer implements SocketServer {
        public void serve(Socket s) {
            try {
                BufferedReader      br      = SocketService.getBufferedReader(s);
                PrintStream         ps      = SocketService.getPrintStream(s);
                String              token   = br.readLine();
                ps.println(token);
            } catch (IOException e) {
            }
        }
    }

    private class WaitThenClose implements SocketServer {
        public void serve(Socket s) {
            ++threadsActive;
            delay();
            --threadsActive;
        }

        private void delay() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
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
                while( (c = reader.read()) != -1 )
                    writer.write(c);
            } catch (IOException e) {
            }
        }
    }
}
