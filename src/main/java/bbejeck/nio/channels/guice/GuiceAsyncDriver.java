package bbejeck.nio.channels.guice;

import com.google.common.base.Stopwatch;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 3/9/12
 * Time: 10:01 PM
 */
public class GuiceAsyncDriver {
    
    public static void main(String[] args) throws  Exception {
        Injector injector = Guice.createInjector(new AsynchronousServerModule());
        AsyncSocketServerGuiceExample socketServer = injector.getInstance(AsyncSocketServerGuiceExample.class);
        System.out.println("Starting the EchoSever");
        Stopwatch stopwatch = new Stopwatch().start();
        socketServer.runServer();
        stopwatch.stop();
        System.out.println("EchoServer is done in "+stopwatch.elapsedMillis());
    }
}
