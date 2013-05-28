package bbejeck.nio;

import bbejeck.nio.channels.AsyncServerSocket;
import bbejeck.nio.channels.AsyncServerTestModule;
import bbejeck.nio.sockets.PlainSocketMessageSender;
import bbejeck.nio.sockets.PlainSocketModule;
import com.google.common.base.Stopwatch;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;


/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 3/14/12
 * Time: 9:48 PM
 */
public class AsyncSocketTestDriver {


    public static void main(String[] args) throws Exception {
        Injector injector = Guice.createInjector(new SocketModule(), new PlainSocketModule(), new AsyncServerTestModule());
        PlainSocketMessageSender messageSender = injector.getInstance(PlainSocketMessageSender.class);
        final AsyncServerSocket asyncServerSocket = injector.getInstance(AsyncServerSocket.class);
        FutureTask<Long> asyncFutureTask = new FutureTask<>(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                Stopwatch stopwatch = new Stopwatch();
                stopwatch.start();
                asyncServerSocket.startServer();
                stopwatch.stop();
                return stopwatch.elapsedMillis();
            }
        });
        System.out.println("Starting the AsyncSocketServer Test");
        new Thread(asyncFutureTask).start();
        long sleepTime = 50;
        Thread.sleep(sleepTime);
        messageSender.sendMessages();
        Long time = asyncFutureTask.get();
        System.out.println("AsyncServer processed [10000] messages  in " + (time - sleepTime) + " millis");
    }
}
