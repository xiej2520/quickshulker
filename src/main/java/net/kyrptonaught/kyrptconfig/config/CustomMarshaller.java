package net.kyrptonaught.kyrptconfig.config;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.api.DeserializationException;
import blue.endless.jankson.impl.MarshallerImpl;

public class CustomMarshaller extends MarshallerImpl {

    public JsonElement serialize(Object obj) {
        if (obj instanceof CustomSerializable customSerializable) {
            return customSerializable.toJson(this);
        }
        return super.serialize(obj);
    }

    public JsonElement serializeNonCustom(Object obj) {
        return super.serialize(obj);
    }

    public CustomSerializable marshallCustomSerializable(Class<CustomSerializable> clazz, CustomSerializable origianlObject, JsonElement elem) throws DeserializationException {
        return origianlObject.fromJson(this, elem, clazz);
    }

    public <T> T marshallNonCustom(Class<T> clazz, JsonElement elem, boolean failFast) throws DeserializationException {
        return super.marshall(clazz, elem, failFast);
    }
}
