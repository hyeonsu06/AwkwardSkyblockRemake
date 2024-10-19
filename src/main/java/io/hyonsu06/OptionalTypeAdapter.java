package io.hyonsu06;

import com.google.gson.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

public class OptionalTypeAdapter implements JsonSerializer<Optional<?>>, JsonDeserializer<Optional<?>> {

    @Override
    public JsonElement serialize(Optional<?> src, Type typeOfSrc, JsonSerializationContext context) {
        return src.isPresent() ? context.serialize(src.get()) : JsonNull.INSTANCE;
    }

    @Override
    public Optional<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return json.isJsonNull() ? Optional.empty() : Optional.of(context.deserialize(json, ((Class<?>) ((ParameterizedType) typeOfT).getActualTypeArguments()[0])));
    }
}
