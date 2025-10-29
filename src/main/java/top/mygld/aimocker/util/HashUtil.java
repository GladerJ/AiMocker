package top.mygld.aimocker.util;

import java.lang.reflect.*;
import java.security.MessageDigest;
import java.util.*;

public class HashUtil {

    public static String computeHash(Class<?> clazz, String prompt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] fieldHash = hashMembers(digest, getFieldSignatures(clazz));
            byte[] ctorHash = hashMembers(digest, getConstructorSignatures(clazz));
            byte[] methodHash = hashMembers(digest, getMethodSignatures(clazz));
            byte[] promptHash = null;
            if(prompt != null)
                promptHash = stringHash(digest, prompt);

            MessageDigest finalDigest = MessageDigest.getInstance("SHA-256");
            finalDigest.update(fieldHash);
            finalDigest.update(ctorHash);
            finalDigest.update(methodHash);
            if(promptHash != null)
                finalDigest.update(promptHash);

            return bytesToHex(finalDigest.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] stringHash(MessageDigest digest, String content){
        digest.reset();
        digest.update(content.getBytes());
        return digest.digest();
    }

    private static byte[] hashMembers(MessageDigest digest, List<String> sigs) {
        Collections.sort(sigs);
        digest.reset();
        for (String s : sigs) digest.update(s.getBytes());
        return digest.digest();
    }

    private static List<String> getFieldSignatures(Class<?> clazz) {
        List<String> list = new ArrayList<>();
        for (Field f : clazz.getDeclaredFields()) {
            list.add(Modifier.toString(f.getModifiers()) + " "
                    + f.getType().getName() + " " + f.getName());
        }
        return list;
    }

    private static List<String> getConstructorSignatures(Class<?> clazz) {
        List<String> list = new ArrayList<>();
        for (Constructor<?> c : clazz.getDeclaredConstructors()) {
            list.add(Modifier.toString(c.getModifiers()) + " "
                    + Arrays.toString(c.getParameterTypes()));
        }
        return list;
    }

    private static List<String> getMethodSignatures(Class<?> clazz) {
        List<String> list = new ArrayList<>();
        for (Method m : clazz.getDeclaredMethods()) {
            list.add(Modifier.toString(m.getModifiers()) + " "
                    + m.getReturnType().getName() + " "
                    + m.getName() + Arrays.toString(m.getParameterTypes()));
        }
        return list;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(computeHash(Demo.class,null));
    }

    static class Demo {
        private long id;
        private String name;
        public Demo(long id) {
        }

        public String getName() {
            return name;
        }
    }


}

