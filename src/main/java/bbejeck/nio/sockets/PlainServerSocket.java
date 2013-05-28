package bbejeck.nio.sockets;

import bbejeck.nio.Host;
import bbejeck.nio.Port;
import com.google.inject.Inject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 3/10/12
 * Time: 12:47 PM
 */
public class PlainServerSocket {

    private ExecutorService executorService;
    private java.net.ServerSocket server;
    private int port;
    private String host;


    @Inject
    public PlainServerSocket(@Port int port, @Host String host) {
        this.port = port;
        this.host = host;
    }


    public void startServer() {
        try {
            initServerSocket();
            this.executorService = Executors.newCachedThreadPool();
            while (!executorService.isShutdown()) {
                Socket socket = server.accept();
                executorService.submit(handleSocketRequest(socket));
            }
        } catch (Exception e) {
            if (!executorService.isShutdown()) {
                e.printStackTrace();
            }
        }
    }

    private Callable<Void> handleSocketRequest(final Socket socket) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                CharBuffer charBuffer = CharBuffer.allocate(1000);
                reader.read(charBuffer);
                charBuffer.flip();
                StringBuilder builder = new StringBuilder("You Said >> \"");
                String message = charBuffer.toString();
//                System.out.println("Plain Socket Message[" + message + "] received @" + new Date());
                builder.append(message).append("\"");
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                bufferedWriter.write(builder.toString());
                bufferedWriter.flush();
                if (message.equalsIgnoreCase("quit")) {
                    stopServer();
                }
                reader.close();
                bufferedWriter.close();
                socket.close();
                return null;
            }
        };
    }

    public void stopServer() throws Exception {
        executorService.shutdownNow();
        server.close();
    }


    private void initServerSocket() throws Exception {
        server = new ServerSocket(port);
    }

}


