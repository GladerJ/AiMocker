package top.mygld.aimocker.junit5;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AiMock {
    /**
     * The natural language scenario describing the mock data to be generated.
     *
     * @return The scenario description.
     */
    String value();
}