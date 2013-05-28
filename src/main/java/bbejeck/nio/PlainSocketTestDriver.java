package bbejeck.nio;

import bbejeck.nio.channels.AsyncServerTestModule;
import bbejeck.nio.sockets.PlainServerSocket;
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
public class PlainSocketTestDriver {


    public static void main(String[] args) throws Exception {
        Injector injector = Guice.createInjector(new SocketModule(), new PlainSocketModule(), new AsyncServerTestModule());
        PlainSocketMessageSender messageSender = injector.getInstance(PlainSocketMessageSender.class);
        System.out.println("Starting the PlainSocketServer Test");
        final PlainServerSocket plainServerSocket = injector.getInstance(PlainServerSocket.class);
        FutureTask<Long> plainFutureTask = new FutureTask<>(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                Stopwatch stopwatch = new Stopwatch();
                stopwatch.start();
                plainServerSocket.startServer();
                stopwatch.stop();
                return stopwatch.elapsedMillis();
            }
        });

        new Thread(plainFutureTask).start();
        long sleepTime = 50;
        Thread.sleep(sleepTime);
        messageSender.sendMessages();
        Long time = plainFutureTask.get();
        System.out.println("PlainSocketServer processed [10000] messages  in " + (time - sleepTime) + " millis");
    }
}
