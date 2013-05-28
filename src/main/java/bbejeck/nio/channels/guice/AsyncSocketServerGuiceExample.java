package bbejeck.nio.channels.guice;

import com.google.inject.Inject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 3/8/12
 * Time: 10:16 PM
 */
public class AsyncSocketServerGuiceExample {

    private CustomCompletionHandler completionHandler;
    private InetSocketAddress inetSocketAddress;
    private CountDownLatch doneSignal;
    private AsynchronousChannelGroup channelGroup;
    private AsynchronousServerSocketChannel server;
    private ExecutorService executorService;

    @Inject
    public AsyncSocketServerGuiceExample(@EchoServer CustomCompletionHandler completionHandler,
                                         @EchoServer int socketAddressPort,
                                         @EchoServer CountDownLatch doneSignal,
                                         @EchoServer ExecutorService executorService) throws IOException {

        this.completionHandler = Objects.requireNonNull(completionHandler);
        this.inetSocketAddress = new InetSocketAddress(socketAddressPort);
        this.doneSignal = Objects.requireNonNull(doneSignal);
        this.executorService = executorService;
        this.channelGroup = AsynchronousChannelGroup.withThreadPool(Objects.requireNonNull(executorService));
    }

    public void runServer() throws Exception {
        server = AsynchronousServerSocketChannel.open(channelGroup);
        completionHandler.setSocketChannel(server);
        server.bind(inetSocketAddress);
        server.accept("AsyncServer", completionHandler);
        waitForQuit();
    }

    private void waitForQuit() {
        try {
            doneSignal.await();
            System.out.println("DoneSignal called");
            channelGroup.shutdown();
            executorService.shutdownNow();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
