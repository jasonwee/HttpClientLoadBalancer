package ch.weetech.client.config.exception;

public class NoServerConfiguredException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
     * Constructs a new runtime exception with the specified detail message.
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
    public NoServerConfiguredException(String message) {
        super(message);
    }

}
