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
        InputStream         is      = s.getInputStream();
        InputStreamReader   isr     = new InputStreamReader(is);
        BufferedReader      br      = new BufferedReader(isr);
        String              answer  = br.readLine();
        assertEquals("Hello", answer);
    }

    public void testReceiveMessage() throws Exception {
        ss.serve(PORT, new EchoService());
        Socket              s       = new Socket("localhost", PORT);
        InputStream         is      = s.getInputStream();
        InputStreamReader   isr     = new InputStreamReader(is);
        BufferedReader      br      = new BufferedReader(isr);
        OutputStream        os      = s.getOutputStream();
        PrintStream         ps      = new PrintStream(os);
        ps.println("MyMessage");
        String              answer  = br.readLine();
        s.close();
        assertEquals("MyMessage", answer);
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

    private class EchoService implements SocketServer {
        public void serve(Socket s) {
            try {
                InputStream         is      = s.getInputStream();
                InputStreamReader   isr     = new InputStreamReader(is);
                BufferedReader      br      = new BufferedReader(isr);
                OutputStream        os      = s.getOutputStream();
                PrintStream         ps      = new PrintStream(os);
                String              token   = br.readLine();
                ps.println(token);
            } catch (IOException e) {
            }
        }
    }
}
