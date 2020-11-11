package ru.javawebinar.topjava.util;

import org.springframework.test.web.servlet.MvcResult;
import ru.javawebinar.topjava.util.exception.TimeUtils;
import ru.javawebinar.topjava.util.json.JsonUtils;

import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.*;
import java.util.List;

public class TestUtils {

    public static <T> T readValueFromMvcResult(MvcResult mvcResult, Class<T> clazz) throws UnsupportedEncodingException {
        return JsonUtils.readValueFromJson(mvcResult.getResponse().getContentAsString(), clazz);
    }

    public static <T> List<T> readValuesFromMvcResult(MvcResult mvcResult, Class<T> clazz) throws UnsupportedEncodingException {
        return JsonUtils.readValuesFromJson(mvcResult.getResponse().getContentAsString(), clazz);
    }

    private static Field clockField;

    //https://stackoverflow.com/questions/56039341/get-declared-fields-of-java-lang-reflect-fields-in-jdk12#answer-56043252
    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(Field.class, MethodHandles.lookup());
            VarHandle modifiers = lookup.findVarHandle(Field.class, "modifiers", int.class);

            clockField = TimeUtils.class.getDeclaredField("CLOCK");
            clockField.setAccessible(true);

            modifiers.set(clockField, clockField.getModifiers() & ~Modifier.FINAL);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void setTime(int hours, int minutes) throws IllegalAccessException {
        clockField.set(null, Clock.fixed(LocalDateTime.of(LocalDate.now(), LocalTime.of(hours, minutes)).toInstant(ZoneOffset.UTC), ZoneId.of("UTC")));
    }
}
