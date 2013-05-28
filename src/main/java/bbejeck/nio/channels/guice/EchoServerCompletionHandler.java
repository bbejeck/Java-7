package bbejeck.nio.channels.guice;

import com.google.inject.Inject;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 3/8/12
 * Time: 10:49 PM
 */

public class EchoServerCompletionHandler implements CustomCompletionHandler {

    private CountDownLatch doneSignal;
    private AsynchronousServerSocketChannel socketChannel;
    //TODO reuse byte  buffers and pass around for lifecycle of request/response
    //TODO use in conjunction with charset encoders/decoders
    //TODO
    @Inject
    public EchoServerCompletionHandler(@EchoServer CountDownLatch doneSignal) {
        this.doneSignal = doneSignal;
    }

    @Override
    public void completed(AsynchronousSocketChannel result, Object attachment) {
        socketChannel.accept(null, this);
        StringBuilder messageBuilder = new StringBuilder("You Said >> ");
        String receivedMessage = readMessage(result);
        System.out.println(Thread.currentThread().getName());

        System.out.println("Received message "+receivedMessage+" sending reply");
        messageBuilder.append(receivedMessage);
        writeEchoMessage(result, messageBuilder.toString());
        if(receivedMessage.equalsIgnoreCase("quit")){
            sendShutdownSignal();
        }
    }

    @Override
    public void failed(Throwable exc, Object attachment) {
        exc.printStackTrace();
    }

    @Override
    public void setSocketChannel(AsynchronousServerSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    private void writeEchoMessage(AsynchronousSocketChannel socket, String message) {
        ByteBuffer byteBuffer = getByteBuffer();
        byteBuffer.put(message.getBytes());
        byteBuffer.flip();
        socket.write(byteBuffer);
    }

    private String readMessage(AsynchronousSocketChannel socket) {
        ByteBuffer byteBuffer = getByteBuffer();
        int totalBytes = 0;
        String message = null;
        //TODO use charset encoders here
        //TODO reuse byteBuffers for lifecycle of entire call
        try {
            totalBytes = socket.read(byteBuffer).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        byteBuffer.flip();
        if (totalBytes > 0) {
            byte[] bytes = new byte[totalBytes];
            byteBuffer.get(bytes);
            message = new String(bytes);
        }
        return message;
    }

    private ByteBuffer getByteBuffer() {
        return ByteBuffer.allocate(1000);
    }

    private void sendShutdownSignal() {
        doneSignal.countDown();
    }
}
