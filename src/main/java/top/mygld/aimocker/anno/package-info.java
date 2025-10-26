/**
 * Annotations for AI-powered mock data generation in JUnit 5 tests.
 *
 * <p>This package provides annotations that enable automatic generation of test data
 * using AI capabilities. It integrates seamlessly with JUnit 5's extension model
 * to inject mock objects into test classes.</p>
 *
 * <h2>Core Annotations:</h2>
 * <ul>
 *   <li>{@link top.mygld.aimocker.anno.AiMockTest} - Enables AI mock functionality for test classes</li>
 *   <li>{@link top.mygld.aimocker.anno.AiMock} - Marks fields or parameters for AI-generated mock data</li>
 * </ul>
 *
 * <h2>Quick Start:</h2>
 * <pre>{@code
 * @AiMockTest
 * class UserServiceTest {
 *
 *     // Field injection - automatically populated before each test
 *     @AiMock("Generate an active user with email")
 *     private User testUser;
 *
 *     @AiMock(value = "Generate premium users", count = 5)
 *     private List<User> premiumUsers;
 *
 *     @Test
 *     void testUserCreation() {
 *         assertNotNull(testUser);
 *         assertNotNull(testUser.getEmail());
 *     }
 *
 *     @Test
 *     void testWithParameter(@AiMock("Generate admin user") User admin) {
 *         assertTrue(admin.isAdmin());
 *     }
 * }
 * }</pre>
 *
 * <h2>Supported Data Types:</h2>
 * <ul>
 *   <li><b>Single Objects:</b> Any POJO class</li>
 *   <li><b>Collections:</b> {@code List<T>}, {@code Set<T>}</li>
 *   <li><b>Arrays:</b> {@code T[]}</li>
 * </ul>
 *
 * <h2>Usage Scenarios:</h2>
 * <p>The AI will generate realistic mock data based on the scenario description provided
 * in the {@code value} attribute of {@code @AiMock}. The more specific the description,
 * the more tailored the generated data will be.</p>
 *
 * <h3>Examples of scenario descriptions:</h3>
 * <ul>
 *   <li>"Generate a new user with valid email and phone number"</li>
 *   <li>"Create premium users from different countries"</li>
 *   <li>"Generate products with prices between $10 and $100"</li>
 *   <li>"Create expired orders for testing refund logic"</li>
 * </ul>
 *
 * <h2>Best Practices:</h2>
 * <ol>
 *   <li>Use descriptive scenario text to get more relevant test data</li>
 *   <li>Specify the {@code count} parameter for collections/arrays when needed</li>
 *   <li>Combine field injection and parameter injection as needed</li>
 *   <li>Keep scenario descriptions concise but informative</li>
 * </ol>
 *
 * <h2>Requirements:</h2>
 * <ul>
 *   <li>JUnit 5 (Jupiter) - version 5.5 or higher</li>
 *   <li>Test classes must be annotated with {@code @AiMockTest}</li>
 *   <li>Valid AI backend configuration in {@link top.mygld.aimocker.AiMocker}</li>
 * </ul>
 *
 * @author Glader
 * @version 1.0
 * @since 1.0
 * @see top.mygld.aimocker.AiMocker
 * @see top.mygld.aimocker.extension
 */
package top.mygld.aimocker.anno;