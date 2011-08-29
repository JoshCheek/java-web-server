import java.io.IOException;
import java.net.Socket;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 8/29/11
 * Time: 10:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class SocketServiceTest extends junit.framework.TestCase {
    private static final int PORT = 1501;

    public void testOneConnection() throws Exception {
        SocketService ss = new SocketService();
        ss.serve(PORT);
        connect(PORT);
        ss.close();
        assertEquals(1, ss.connections());
    }

    public void testManyConnections() throws Exception {
        SocketService ss = new SocketService();
        ss.serve(PORT);
        for( int i = 0; i < 10; ++i )
            connect(PORT);
        ss.close();
        assertEquals(10, ss.connections());
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
}
