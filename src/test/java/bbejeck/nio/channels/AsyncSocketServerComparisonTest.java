package bbejeck.nio.channels;

import bbejeck.nio.sockets.PlainServerSocket;
import bbejeck.nio.sockets.PlainSocketMessageSender;
import com.google.common.base.Stopwatch;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 3/15/12
 * Time: 11:27 PM
 */
@Ignore
public class AsyncSocketServerComparisonTest {


    @Test
    public void testAsyncSocketServer() throws Exception {
        PlainSocketMessageSender messageSender = new PlainSocketMessageSender(5000,10000,"localhost");
        final AsyncServerSocket serverSocket = new AsyncServerSocket(5000,"localhost");
        FutureTask<Long> asyncFutureTask = new FutureTask<>(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                Stopwatch stopwatch = new Stopwatch();
                stopwatch.start();
                serverSocket.startServer();
                stopwatch.stop();
                return stopwatch.elapsedTime(TimeUnit.SECONDS);
            }
        });
        System.out.println("Starting the AsyncSocketServer Test");
        new Thread(asyncFutureTask).start();
        Thread.sleep(1000);
        messageSender.sendMessages();
        Long time = asyncFutureTask.get();
        System.out.println("AsyncServer processed [10000] messages  in " + time+" seconds");
    }
    
    @Test
    @Ignore
    public void testPlainSocketServer() throws Exception {
        PlainSocketMessageSender messageSender = new PlainSocketMessageSender(5001,10000,"localhost");
        final PlainServerSocket serverSocket = new PlainServerSocket(5001,"localhost");
        FutureTask<Long> plainFutureTask = new FutureTask<>(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                Stopwatch stopwatch = new Stopwatch();
                stopwatch.start();
                serverSocket.startServer();
                stopwatch.stop();
                return stopwatch.elapsedTime(TimeUnit.SECONDS);
            }
        });

        new Thread(plainFutureTask).start();
        Thread.sleep(1000);
        messageSender.sendMessages();
        Long time = plainFutureTask.get();
        System.out.println("PlainSocketServer processed [10000] messages  in " + time +" seconds");
    }

}
