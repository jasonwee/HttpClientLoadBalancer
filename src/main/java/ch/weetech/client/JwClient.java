package ch.weetech.client;

import java.io.Closeable;
import java.io.IOException;
import java.util.Set;

import ch.weetech.action.Action;

public interface JwClient extends Closeable {

    <T extends JwResult> T execute(Action<T> clientRequest) throws IOException;

    //<T extends JwResult> void executeAsync(Action<T> clientRequest, JestResultHandler<? super T> jestResultHandler);

    /**
     * @deprecated Use {@link #close()} instead.
     */
    @Deprecated
    void shutdownClient();

    void setServers(Set<String> servers);

}
