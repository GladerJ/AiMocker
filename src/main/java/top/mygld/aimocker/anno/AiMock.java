package top.mygld.aimocker.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to generate AI-powered mock data for test parameters and fields.
 *
 * <p>Supports:</p>
 * <ul>
 *   <li>Single objects</li>
 *   <li>Collections (List, Set)</li>
 *   <li>Arrays</li>
 *   <li>Test method parameters</li>
 *   <li>Test class fields</li>
 * </ul>
 *
 * <h2>Example Usage:</h2>
 * <pre>{@code
 * @AiMockTest
 * class UserTest {
 *     // Field injection
 *     @AiMock("Generate active user")
 *     private User user;
 *
 *     @AiMock(value = "Generate premium users", count = 5)
 *     private List<User> users;
 *
 *     // Parameter injection
 *     @Test
 *     void test(@AiMock("Generate admin user") User admin) {
 *         // ...
 *     }
 * }
 * }</pre>
 * @Author
 *  Glader
 * @Since 1.0
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})  // 关键：必须包含 FIELD
@Retention(RetentionPolicy.RUNTIME)
public @interface AiMock {

    /**
     * Scenario or description for AI to generate mock data.
     *
     * @return the scenario description
     */
    String value() default "";

    /**
     * Number of items to generate for collections or arrays.
     * Ignored for single object parameters.
     *
     * @return the count, default is 1
     */
    int count() default 1;
}