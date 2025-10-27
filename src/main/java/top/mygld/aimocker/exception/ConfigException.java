package top.mygld.aimocker.exception;
/**
 * Exception thrown when configuration loading or parsing fails.
 *
 * @author Glader
 * @since 1.0
 */
public class ConfigException extends AiMockerException {

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
