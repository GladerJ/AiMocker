package top.mygld.aimocker.exception;

/**
 * Exception thrown when cache operations fail.
 *
 * @author Glader
 * @since 1.0
 */
public class CacheException extends AiMockerException {

    public CacheException(String message) {
        super(message);
    }

    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }
}