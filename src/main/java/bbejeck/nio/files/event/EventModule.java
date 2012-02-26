package bbejeck.nio.files.event;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 2/25/12
 * Time: 10:09 PM
 */
public class EventModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(EventBus.class).in(Singleton.class);
    }
}
