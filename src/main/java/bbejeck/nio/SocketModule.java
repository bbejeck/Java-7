package bbejeck.nio;

import com.google.inject.AbstractModule;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 3/14/12
 * Time: 9:40 PM
 */
public class SocketModule  extends AbstractModule {

    @Override
    protected void configure() {
          bind(String.class).annotatedWith(Host.class).toInstance("localhost");
          bind(int.class).annotatedWith(MessageCount.class).toInstance(10000);
          bind(int.class).annotatedWith(Port.class).toInstance(8080);
    }
}
