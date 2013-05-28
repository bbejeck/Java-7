package bbejeck.nio.channels.guice;

import com.google.inject.AbstractModule;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 3/6/12
 * Time: 10:24 PM
 */
public class AsynchronousServerModule extends AbstractModule {
    private CountDownLatch doneSignal = new CountDownLatch(1);
    private ExecutorService executorService = Executors.newFixedThreadPool(25);

    @Override
    protected void configure() {
        bind(CountDownLatch.class).annotatedWith(EchoServer.class).toInstance(doneSignal);
        bind(int.class).annotatedWith(EchoServer.class).toInstance(5000);
        bind(ExecutorService.class).annotatedWith(EchoServer.class).toInstance(executorService);
        bind(CustomCompletionHandler.class).annotatedWith(EchoServer.class).to(EchoServerCompletionHandler.class);
    }

}
