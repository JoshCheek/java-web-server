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
    public void testOneConnection() {
        SocketService ss = new SocketService();
        ss.serve(1501);
        connect(1501);
        ss.close();
        assertEquals(1, ss.connections());
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
