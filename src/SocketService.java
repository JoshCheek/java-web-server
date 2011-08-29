import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 8/29/11
 * Time: 10:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class SocketService {
    private ServerSocket    serverSocket = null;
    private int             connections  = 0;
    private Thread          serverThread = null;
    private boolean         running      = false;


    public void serve(int port) throws Exception {
        serverSocket = new ServerSocket(port);
        serverThread = new Thread(
            new Runnable() {
                    public void run() {
                        running = true;
                        while (running) {
                            try {
                                Socket socket = serverSocket.accept();
                                socket.close();
                                ++connections;
                            } catch (IOException e) {
                            }
                        }
                    }
            }
        );
        serverThread.start();
    }

    public void close() throws IOException {
        running = false;
        serverSocket.close();
    }

    public int connections() {
        return connections;
    }
}
