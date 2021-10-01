package ch.weetech.client.config.exception;

import java.io.IOException;

public class CouldNotConnectException extends IOException {

    private final String host;

    public CouldNotConnectException(String host, Throwable cause) {
        super("Could not connect to " + host, cause);
        this.host = host;
    }

    public String getHost() {
        return host;
    }

}
