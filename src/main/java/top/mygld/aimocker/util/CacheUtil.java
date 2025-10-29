package top.mygld.aimocker.util;

import java.io.File;
import java.util.*;

public class CacheUtil {

    public static void writeCache(Class<?> clazz, String prompt, Object obj) {
        JsonUtil.writeJson("~/.aimocker/cache/"
                + HashUtil.computeHash(clazz, prompt)
                + "/" + System.currentTimeMillis() + ".json", obj);
    }

    public static Object readCache(Class<?> clazz, String prompt) {
        File[] files = getFiles(clazz, prompt);

        if (files == null || files.length == 0) {
            return null;
        }

        File selected = files[new Random().nextInt(files.length)];
        return JsonUtil.readJson(selected.getAbsolutePath(), clazz);
    }

    public static List<Object> readCacheCollection(Class<?> clazz, String prompt, int count) {
        File[] files = getFiles(clazz, prompt);

        if (files == null || files.length == 0) {
            return null;
        }

        List<Object> list = new ArrayList<Object>();

        if (files.length <= count) {
            for (File file : files) {
                list.add(JsonUtil.readJson(file.getAbsolutePath(), clazz));
            }
            return list;
        } else {
            List<Integer> randomList = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                randomList.add(i);
            }
            Collections.shuffle(randomList);

            for(int i = 0;i < count;i++){
                list.add(JsonUtil.readJson(files[randomList.get(i)].getAbsolutePath(),clazz));
            }
            return list;
        }
    }

    private static File[] getFiles(Class<?> clazz, String prompt) {
        File file = new File(PathUtil.resolve("~/.aimocker/cache/" + HashUtil.computeHash(clazz, prompt)));

        if (!file.exists()) {
            return null;
        }

        File[] files = file.listFiles();

        if (files == null || files.length == 0) {
            return null;
        }

        return files;
    }

}
