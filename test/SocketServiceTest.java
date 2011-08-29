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

    public SocketServiceTest(String name) {
        super(name);
        connectionCounter = new SocketServer() {
            public void serve(Socket s) {
                 ++connections;
            }
        };
    }

    public void testOneConnection() throws Exception {
        SocketService ss = new SocketService();
        ss.serve(PORT, connectionCounter);
        connect(PORT);
        ss.close();
        assertEquals(1, connections);
    }

    public void testManyConnections() throws Exception {
        SocketService ss = new SocketService();
        ss.serve(PORT, connectionCounter);
        for( int i = 0; i < 10; ++i )
            connect(PORT);
        ss.close();
        assertEquals(10, connections);
    }

    public void testSendMessage() throws Exception {
        SocketService ss = new SocketService();
        ss.serve(PORT, new HelloServer());
        Socket              s       = new Socket("localhost", PORT);
        InputStream         is      = s.getInputStream();
        InputStreamReader   isr     = new InputStreamReader(is);
        BufferedReader      br      = new BufferedReader(isr);
        String              answer  = br.readLine();
        s.close();
        assertEquals("Hello", answer);
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
}
