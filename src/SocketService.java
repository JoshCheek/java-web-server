import java.io.*;
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
    private SocketServer    itsServer    = null;

    public static BufferedReader getBufferedReader(Socket s) throws IOException {
        InputStream is = s.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        return new BufferedReader(isr);
    }

    public static PrintStream getPrintStream(Socket s) throws IOException {
        return new PrintStream(s.getOutputStream());
    }

    public void serve(int port, SocketServer server) throws Exception {
        itsServer    = server;
        serverSocket = new ServerSocket(port);
        serverThread = makeServerThread();
        serverThread.start();
    }

    private Thread makeServerThread() {
        return new Thread(
            new Runnable() {
                public void run() {
                    running = true;
                    while (running)
                        acceptServeAndConnect();
                }
            }
        );
    }

    private void acceptServeAndConnect() {
        try {
            Socket socket = serverSocket.accept();
            itsServer.serve(socket);
            socket.close();
            ++connections;
        } catch (IOException e) {
        }
    }

    public void close() throws IOException {
        running = false;
        serverSocket.close();
    }

    public int connections() {
        return connections;
    }
}
