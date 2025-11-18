package top.mygld.aimocker;

import org.junit.jupiter.api.Test;
import top.mygld.aimocker.anno.AiMock;

import top.mygld.aimocker.anno.AiMockTest;

import top.mygld.aimocker.pojo.Animal;
import top.mygld.aimocker.pojo.User;
import top.mygld.aimocker.util.CacheUtil;
import top.mygld.aimocker.util.HashUtil;
import top.mygld.aimocker.util.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static top.mygld.aimocker.util.ResourceUtil.getProperties;
import static top.mygld.aimocker.util.ResourceUtil.getYaml;

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
    void testAiMockUser(@AiMock(value = "数据自拟，答案随机一点，不要固定，都不能为空") User aVipUser) {
        System.out.println(aVipUser);

    }

    @Test
    void testListAiMockUser(@AiMock(value = "数据自拟，答案随机一点，不要固定，都不能为空",count = 5,cache = true) List<User> users) {
        System.out.println(users);
    }

    @Test
    void testArrayAiMockUser(
            @AiMock(value = "生成随机数据，要求非常随机，不能只固定一个或一种，随机一点，各个国家的城市都有"
                    ,count = 5) ArrayList<User> users) {
        System.out.println(users);
    }

    @Test
    void testListAiMockUser(){
        System.out.println(CacheUtil.readCache(User.class,"数据自拟，答案随机一点，不要固定，都不能为空"));
    }

    //SILICONFLOW_API_KEY
    @Test
    void testEnv() throws IOException {
        //Map<String, String> map = System.getenv();
        System.out.println(getYaml("aimocker.yml", "llm"));
//        System.out.println(getProperties("aimocker.properties"));
        //System.out.println(map);
    }
}

