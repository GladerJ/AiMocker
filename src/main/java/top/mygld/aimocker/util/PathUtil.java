package top.mygld.aimocker.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtil {

    public static String resolve(String path){
        if(path == null || path.isEmpty()){
            throw new IllegalArgumentException("Path is null or empty");
        }

        if(path.startsWith("~")){
            path = System.getProperty("user.home") + path.substring(1);
        }

        return Paths.get(path).toAbsolutePath().normalize().toString();
    }

    public static void main(String[] args) {
        System.out.println(resolve("~/hello"));
    }
}
