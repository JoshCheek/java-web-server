package joshcheek.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 8/29/11
 * Time: 10:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class SocketService {
    private ServerSocket    serverSocket  = null;
    private Thread          serverThread  = null;
    private boolean         running       = false;
    private SocketServer    itsServer     = null;
    private List            serverThreads = Collections.synchronizedList(new LinkedList());

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
            Thread serverThread = new Thread(new ServiceRunnable(socket));
            serverThreads.add(serverThread);
            serverThread.start();
        } catch (IOException e) {
        }
    }

    public void close() throws Exception {
        if(running) {
            running = false;
            serverSocket.close();
            serverThread.join();
            while(serverThreads.size() > 0) {
                Thread thread = (Thread) serverThreads.get(0);
                serverThreads.remove(thread);
                thread.join();
            }
        } else {
            serverSocket.close();
        }
    }


    private class ServiceRunnable implements Runnable {
        private Socket itsSocket;

        public ServiceRunnable(Socket socket) {
            itsSocket = socket;
        }

        public void run() {
            try {
                itsServer.serve(itsSocket);
                serverThreads.remove(Thread.currentThread());
                itsSocket.close();
            } catch (IOException e) {
            }
        }
    }
}
