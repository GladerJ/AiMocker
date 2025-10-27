package top.mygld.aimocker.exception;

/**
 * Base exception for AiMocker framework.
 * All custom exceptions should extend this class.
 *
 * @author Glader
 * @since 1.0
 */
public class AiMockerException extends RuntimeException {

    public AiMockerException(String message) {
        super(message);
    }

    public AiMockerException(String message, Throwable cause) {
        super(message, cause);
    }

    public AiMockerException(Throwable cause) {
        super(cause);
    }
}
