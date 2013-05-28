package bbejeck.nio.channels;

import com.google.common.base.Stopwatch;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 3/4/12
 * Time: 10:25 PM
 */
public class AsyncServerSocketNoCompletionHander {

    static int count = 0;
    static AsynchronousChannelGroup channelGroup;

    public static void main(String[] args) throws Exception {
        channelGroup = AsynchronousChannelGroup.withThreadPool(Executors.newFixedThreadPool(5));
        final AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(channelGroup);
        server.bind(new InetSocketAddress(5000));
        Stopwatch stopwatch = new Stopwatch().start();
        while (!channelGroup.isShutdown()) {
            AsynchronousSocketChannel socket = server.accept().get();
            processConnection(socket);
        }
        stopwatch.stop();
        System.out.println("EchoServer is done in " + stopwatch.elapsedMillis());
    }

    private static void processConnection(AsynchronousSocketChannel socket) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1000);
        Future<Integer> read = socket.read(byteBuffer);
        int totalBytes = 0;
        try {
            totalBytes = read.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        //TODO use charset encoders here USE UTF8
        byteBuffer.flip();
        byte[] bytes = new byte[totalBytes];
        if (bytes.length > 0) {
            byteBuffer.get(bytes, 0, totalBytes);
            String message = new String(bytes);
            System.out.println(Thread.currentThread().getName());
            System.out.println("Message " + message);
            if (message.equals("quit")) {
                quit();
            }
        }
    }

    private static void quit() {
        channelGroup.shutdown();
    }
}
