package bbejeck.nio.channels;

import com.google.inject.AbstractModule;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 3/15/12
 * Time: 10:35 PM
 */

public class AsyncServerTestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AsyncServerSocket.class);
    }
}
