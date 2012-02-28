package bbejeck.nio.files.directory.event;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 2/25/12
 * Time: 9:30 PM
 */

public class DirectoryEventModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(EventBus.class).in(Singleton.class);
        bind(String.class).annotatedWith(Names.named("START_PATH")).toInstance("test-files");
        bind(DirectoryEventWatcher.class).toProvider(DirectoryEventWatcherProvider.class);
    }
}
