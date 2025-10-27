package top.mygld.aimocker.exception;

/**
 * Exception thrown when AI mock data generation fails.
 *
 * @author Glader
 * @since 1.0
 */
public class GenerationException extends AiMockerException {

    public GenerationException(String message) {
        super(message);
    }

    public GenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}