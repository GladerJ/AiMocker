package top.mygld.aimocker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ParameterResolver;
import top.mygld.aimocker.anno.AiMock;

import top.mygld.aimocker.pojo.User;
import top.mygld.aimocker.resolver.AiMockParameterResolver;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
@ExtendWith(AiMockParameterResolver.class)
public class JUnitExtensionTest {
    @Test
    void testParameterResolverExists() {

        assertTrue(ParameterResolver.class != null);
    }

    @Test
    void testAiMockUser(@AiMock("数据自拟，答案随机一点，不要固定，都不能为空") User aVipUser) {
        System.out.println(aVipUser);
    }

    @Test
    void testListAiMockUser(@AiMock(value = "数据自拟，答案随机一点，不要固定，都不能为空",count = 5) List<User> users) {
        System.out.println(users);
    }
}

