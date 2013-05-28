package bbejeck.nio.channels;

import bbejeck.nio.Host;
import bbejeck.nio.MessageCount;
import bbejeck.nio.Port;
import com.google.common.base.Stopwatch;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 3/4/12
 * Time: 11:01 PM
 */
public class AsyncSocketMessageSender {

    private int port;
    private int numberMessages;
    private String host;
    private AsynchronousChannelGroup channelGroup;

    public AsyncSocketMessageSender(@Port int port, @MessageCount int numberMessages, @Host String host) {
        this.port = port;
        this.numberMessages = numberMessages;
        this.host = host;

    }

    public void sendMessages() throws Exception {
        channelGroup = AsynchronousChannelGroup.withThreadPool(Executors.newCachedThreadPool());
        String sampleMessage = "Text message sent from socket";
        AsynchronousSocketChannel socketChannel = null;
        for (int i = 0; i < numberMessages; i++) {
            Thread.sleep(1);
            socketChannel = AsynchronousSocketChannel.open(channelGroup);
            socketChannel.connect(new InetSocketAddress(host, port), null, getHandler(getByteBuffer(sampleMessage), socketChannel));
        }

        socketChannel = AsynchronousSocketChannel.open(channelGroup);
        socketChannel.connect(new InetSocketAddress(port), null, getHandler(getByteBuffer("quit"), socketChannel));
        channelGroup.shutdown();
    }

    private ByteBuffer getByteBuffer(String message) {
        ByteBuffer buffer = ByteBuffer.allocate(1000);
        buffer.put(message.getBytes(Charset.forName("UTF-8")));
        buffer.flip();
        return buffer;
    }


    private CompletionHandler getHandler(final ByteBuffer messageBuffer, final AsynchronousSocketChannel socketChannel) {
        return new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(AsynchronousSocketChannel result, Object attachment) {
                socketChannel.write(messageBuffer);
                messageBuffer.clear();
                Future<Integer> bytesRead = socketChannel.read(messageBuffer);
                try {
                    int numBytes = bytesRead.get();
                    byte[] bytes = new byte[numBytes];
                    messageBuffer.flip();
                    messageBuffer.get(bytes, 0, numBytes);
                    String reply = new String(bytes,Charset.forName("UTF-8"));
                    System.out.println("Server Reply " + reply);
                    socketChannel.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("FAILED");
                exc.printStackTrace();
            }
        };
    }

    public static void main(String[] args) throws Exception {
        AsyncSocketMessageSender messageSender = new AsyncSocketMessageSender(5000, 1000, "localhost");
        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();
        messageSender.sendMessages();
        stopwatch.stop();
        System.out.println("Done sending messages in " + stopwatch.elapsedTime(TimeUnit.SECONDS));
    }

}
