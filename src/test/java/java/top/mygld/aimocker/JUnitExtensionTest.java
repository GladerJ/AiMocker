package top.mygld.aimocker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ParameterResolver;
import top.mygld.aimocker.junit5.AiMock;

import top.mygld.aimocker.pojo.User;

import static org.junit.jupiter.api.Assertions.assertTrue;
@ExtendWith(top.mygld.aimocker.junit5.AiMockerExtension.class)
public class JUnitExtensionTest {
    @Test
    void testParameterResolverExists() {

        assertTrue(ParameterResolver.class != null);
    }

    @Test
    void testAiMockUser(@AiMock("一个来自北京、名叫李明的VIP用户，其他数据自拟，都不能为空") User aVipUser) {
        System.out.println(aVipUser);
    }
}
