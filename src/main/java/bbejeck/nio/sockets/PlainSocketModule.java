package bbejeck.nio.sockets;

import com.google.inject.AbstractModule;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 3/10/12
 * Time: 12:57 PM
 */
public class PlainSocketModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(PlainServerSocket.class);
        bind(PlainSocketMessageSender.class);
    }
}
