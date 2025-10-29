package top.mygld.aimocker;

import org.junit.jupiter.api.Test;
import top.mygld.aimocker.anno.AiMock;

import top.mygld.aimocker.anno.AiMockTest;

import top.mygld.aimocker.pojo.Animal;
import top.mygld.aimocker.pojo.User;
import top.mygld.aimocker.util.CacheUtil;
import top.mygld.aimocker.util.HashUtil;
import top.mygld.aimocker.util.JsonUtil;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
@AiMockTest
public class JUnitExtensionTest {
//
    @AiMock("Generate mock user data")
    private User user1;

    @AiMock("Generate mock animal data")
    private Animal animal;



    @Test
    void testParameterResolverExists() {
        System.out.println(animal);
    }

    @Test
    void testAiMockUser(@AiMock(value = "数据自拟，答案随机一点，不要固定，都不能为空",cache = true) User aVipUser) {
        System.out.println(aVipUser);

    }

    @Test
    void testListAiMockUser(@AiMock(value = "数据自拟，答案随机一点，不要固定，都不能为空",count = 5,cache = true) List<User> users) {
        System.out.println(users);
    }

    @Test
    void testArrayAiMockUser(
            @AiMock(value = "生成随机数据，要求非常随机，不能只固定一个或一种，随机一点，各个国家的城市都有"
                    ,count = 6,cache = true) User[] users) {
        System.out.println(Arrays.toString(users));
    }

    @Test
    void testListAiMockUser(){
        System.out.println(CacheUtil.readCache(User.class,"数据自拟，答案随机一点，不要固定，都不能为空"));
    }
}

