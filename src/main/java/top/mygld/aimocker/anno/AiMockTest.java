package top.mygld.aimocker.anno;

import org.junit.jupiter.api.extension.ExtendWith;
import top.mygld.aimocker.extension.AiMockFieldInjector;
import top.mygld.aimocker.extension.AiMockParameterResolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables the AI Mock framework in JUnit 5 test classes.
 * <p>
 * This annotation is a wrapper around {@link org.junit.jupiter.api.extension.ExtendWith}
 * that automatically registers the {@link top.mygld.aimocker.extension.AiMockParameterResolver}.
 * It allows test classes to automatically resolve and inject fields or parameters
 * annotated with {@link top.mygld.aimocker.anno.AiMock}.
 * </p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Simplifies test configuration by eliminating the need to manually declare
 *       {@code @ExtendWith(AiMockParameterResolver.class)}.</li>
 *   <li>Automatically generates and injects mock data during test execution.</li>
 *   <li>Works seamlessly with {@link top.mygld.aimocker.anno.AiMock} for AI-powered
 *       data generation on POJOs, collections, or method parameters.</li>
 * </ul>
 *
 * <h2>Example Usage:</h2>
 * <pre>{@code
 * @AiMockTest
 * class UserServiceTest {
 *
 *     @AiMock("Generate mock user data")
 *     private User user;
 *
 *     @Test
 *     void testUserData() {
 *         System.out.println(user);
 *     }
 * }
 * }</pre>
 *
 * @author
 *     Glader
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({AiMockParameterResolver.class, AiMockFieldInjector.class})
public @interface AiMockTest {
}
