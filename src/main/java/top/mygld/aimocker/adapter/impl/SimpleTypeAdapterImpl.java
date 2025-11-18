package top.mygld.aimocker.adapter.impl;

import top.mygld.aimocker.adapter.SimpleTypeAdapter;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * SimpleTypeAdapter default implement class.
 */
public class SimpleTypeAdapterImpl implements SimpleTypeAdapter {

    @Override
    public boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive()
                || type.isEnum()
                || type == String.class
                || type == Boolean.class || type == Character.class || type == Byte.class
                || type == Short.class || type == Integer.class || type == Long.class
                || type == Float.class || type == Double.class
                || java.util.Date.class.isAssignableFrom(type)
                || java.time.temporal.Temporal.class.isAssignableFrom(type)  // LocalDate/Time/DateTime
                || type.getName().startsWith("java.")
                || type.getName().startsWith("javax.")
                || type == BigDecimal.class || type == BigInteger.class;
    }
}
