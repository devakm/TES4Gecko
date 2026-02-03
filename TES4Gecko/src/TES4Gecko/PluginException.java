package TES4Gecko;

/**
 * The PluginException class defines the exceptions thrown while processing
 * a plugin file.
 */
public class PluginException extends Exception {

    /**
     * Construct an exception with no message text or causing exception
     */
    public PluginException() {
        super();
    }

    /**
     * Construct an exception with message text but no causing exception
     *
     * @param       exceptionMsg    The message text for the exception
     */
    public PluginException(String exceptionMsg) {
        super(exceptionMsg);
    }

    /**
     * Construct an exception with message text and a causing exception
     *
     * @param       exceptionMsg    The message text for the exception
     * @param       cause           The causing exception
     */
    public PluginException(String exceptionMsg, Throwable cause) {
        super(exceptionMsg, cause);
    }
}
