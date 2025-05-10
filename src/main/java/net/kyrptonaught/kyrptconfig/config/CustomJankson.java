package net.kyrptonaught.kyrptconfig.config;

import blue.endless.jankson.Jankson;

import java.lang.reflect.Field;

public class CustomJankson {
    public static Jankson.Builder customJanksonBuilder() {
        return new Jankson.Builder();
    }

    public static Boolean shouldSerializeField(Object t, Field field) {
        if (t instanceof CustomSerializable) {
            return ((CustomSerializable) t).shouldSerializeField(field);
        }
        return true;
    }
}
