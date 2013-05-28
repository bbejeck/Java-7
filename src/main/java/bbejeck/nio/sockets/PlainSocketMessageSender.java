package bbejeck.nio.sockets;

import bbejeck.nio.Host;
import bbejeck.nio.MessageCount;
import bbejeck.nio.Port;
import com.google.inject.Inject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.CharBuffer;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 3/10/12
 * Time: 1:18 PM
 */
public class PlainSocketMessageSender {

    private Socket socket;
    private int port;
    private InetSocketAddress socketAddress;
    private int messageCount;

    @Inject
    public PlainSocketMessageSender(@Port int port, @MessageCount int messageCount, @Host String host) {
        this.port = port;
        socketAddress = new InetSocketAddress(host, port);
        this.messageCount = messageCount;
    }

    public void sendMessages() throws Exception {
        for (int i = 0; i < messageCount; i++) {
            sendTextMessage("Plain text message from socket");
        }
        sendTextMessage("quit");
    }

    private void sendTextMessage(String message) throws Exception {
        socket = new Socket();
        socket.connect(socketAddress);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bufferedWriter.write(message);
        bufferedWriter.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        CharBuffer charBuffer = CharBuffer.allocate(1000);
        reader.read(charBuffer);
        charBuffer.flip();
//        System.out.println("Reply from server "+charBuffer.toString());
        reader.close();
        bufferedWriter.close();
        socket.close();
    }
}
