package top.mygld.aimocker.anno;

import top.mygld.aimocker.enums.MockType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for generating AI mock data for a method parameter.
 * <p>
 * Can generate single objects, collections, or arrays according to {@link MockType}.
 * </p>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AiMock {

    /**
     * The natural language description of the mock data scenario.
     * Used by the AI generator to guide the mock data creation.
     *
     * @return the scenario description
     */
    String value();

    /**
     * The number of mock items to generate.
     * Default is 1 (single object).
     * If the parameter type is a collection or array, this determines the number of elements.
     *
     * @return number of items to generate
     */
    int count() default 1;

    /**
     * The mode of mock generation.
     * <ul>
     *     <li>SINGLE: generate a single object</li>
     *     <li>COLLECTION: generate a collection of objects</li>
     *     <li>ARRAY: generate an array of objects</li>
     * </ul>
     *
     * @return the mock generation type
     */
    MockType type() default MockType.SINGLE;

    /**
     * Whether null values are allowed in the generated mock data.
     *
     * @return true if null values are allowed, false otherwise
     */
    boolean allowNull() default false;
}
