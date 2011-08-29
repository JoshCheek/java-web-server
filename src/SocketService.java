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


    public void serve(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
        }
        serverThread = new Thread(
                new Runnable() {
                    public void run() {
                        try {

                            Socket socket = serverSocket.accept();
                            socket.close();
                            ++connections;
                        } catch (IOException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }
                }
        );
        serverThread.start();
    }

    public void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public int connections() {
        return connections;
    }
}
