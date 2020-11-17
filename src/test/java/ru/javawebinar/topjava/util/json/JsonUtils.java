package ru.javawebinar.topjava.util.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.javawebinar.topjava.util.JacksonObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class JsonUtils {

    private JsonUtils() {
    }

    private static final ObjectMapper MAPPER = JacksonObjectMapper.getMapper();

    public static <T> String writeValueToJson(T obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid write to JSON:\n'" + obj + "'", e);
        }
    }

    public static <T> String writeValueToJsonWithAdditionalProp(T obj, String propName, String propValue) {
        try {
            Map<String, Object> map = MAPPER.convertValue(obj, new TypeReference<>() {
            });
            map.put(propName, propValue);
            return MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid write to JSON:\n'" + obj + "'", e);
        }
    }

    public static <T> T readValueFromJson(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid read from JSON:\n'" + json + "'", e);
        }
    }

    public static <T> List<T> readValuesFromJson(String json, Class<T> clazz) {
        try {
            return MAPPER.readerFor(clazz).<T>readValues(json).readAll();
        } catch (IOException e) {
            throw new IllegalStateException("Invalid read from JSON:\n'" + json + "'", e);
        }
    }

}
