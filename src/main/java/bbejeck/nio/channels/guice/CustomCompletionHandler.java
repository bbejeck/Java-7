package bbejeck.nio.channels.guice;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 3/9/12
 * Time: 10:46 PM
 */

public interface CustomCompletionHandler extends CompletionHandler<AsynchronousSocketChannel, Object> {
    void setSocketChannel(AsynchronousServerSocketChannel serverChannel);
}
