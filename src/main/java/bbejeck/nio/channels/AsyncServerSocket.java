package bbejeck.nio.channels;

import bbejeck.nio.Host;
import bbejeck.nio.Port;
import com.google.inject.Inject;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.*;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 3/4/12
 * Time: 10:25 PM
 */

public class AsyncServerSocket {

    private int count = 0;
    private CountDownLatch latch = new CountDownLatch(1);
    private int port;
    private String host;
    private AsynchronousChannelGroup channelGroup;
    private AsynchronousServerSocketChannel server;

    @Inject
    public AsyncServerSocket(@Port int port, @Host String host) throws Exception {
        this.port = port;
        this.host = host;
    }

    public void startServer() throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        channelGroup = AsynchronousChannelGroup.withThreadPool(executorService);
        server = AsynchronousServerSocketChannel.open(channelGroup);
        server.bind(new InetSocketAddress(host, port));
        server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(AsynchronousSocketChannel result, Object attachment) {
                server.accept(null, this);
                processConnection(result);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });
        latch.await();
        channelGroup.shutdown();
        executorService.shutdownNow();
        System.out.println("AsyncServer shutting down");
    }

    private void processConnection(AsynchronousSocketChannel socket) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1000);
        Future<Integer> read = socket.read(byteBuffer);
        try {
            int totalBytes = read.get();
            String message = getMessageFromBuffer(byteBuffer, totalBytes);
//            System.out.println("Message[" + message + "] received @" + new Date());
            sendEchoResponse(byteBuffer, socket, message);
            if (message.equals("quit")) {
                quit();
            }
        } catch (InterruptedException | ExecutionException e) {
            quit();
            throw new RuntimeException(e);
        }
    }

    private void sendEchoResponse(ByteBuffer byteBuffer, AsynchronousSocketChannel socket, String originalMessage) {
        StringBuilder builder = new StringBuilder("You Said >> \"");
        builder.append(originalMessage).append("\"");
        byteBuffer.clear();
        byteBuffer.put(builder.toString().getBytes(Charset.forName("UTF-8")));
        byteBuffer.flip();
        socket.write(byteBuffer);
    }


    private String getMessageFromBuffer(ByteBuffer byteBuffer, int size) {
        byte[] bytes = new byte[size];
        byteBuffer.flip();
        byteBuffer.get(bytes, 0, size);
        return new String(bytes, Charset.forName("UTF-8"));
    }

    private void quit() {
        latch.countDown();
    }

}
