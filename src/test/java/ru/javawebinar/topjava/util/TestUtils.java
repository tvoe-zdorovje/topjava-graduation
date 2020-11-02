package ru.javawebinar.topjava.util;

import org.assertj.core.api.Assertions;
import org.springframework.test.web.servlet.MvcResult;
import ru.javawebinar.topjava.util.json.JsonUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class TestUtils {

    public static <T> T readValueFromMvcResult(MvcResult mvcResult, Class<T> clazz) throws UnsupportedEncodingException {
        return JsonUtils.readValueFromJson(mvcResult.getResponse().getContentAsString(), clazz);
    }

    public static <T> List<T> readValuesFromMvcResult(MvcResult mvcResult, Class<T> clazz) throws UnsupportedEncodingException {
        return JsonUtils.readValuesFromJson(mvcResult.getResponse().getContentAsString(), clazz);
    }

    public static class TestMatcher<T> {
        private final String[] fieldsToIgnore;

        public TestMatcher(String... fieldsToIgnore) {
            this.fieldsToIgnore = fieldsToIgnore;
        }

        public void assertMatch(T actual, T expected) {
            Assertions.assertThat(actual).usingRecursiveComparison().ignoringFields(fieldsToIgnore).isEqualTo(expected);
        }

        @SafeVarargs
        public final void assertMatch(List<T> actual, T... expected) {
            assertMatch(actual, List.of(expected));
        }

        public void assertMatch(List<T> actual, List<T> expected) {
            Assertions.assertThat(actual).usingElementComparatorIgnoringFields(fieldsToIgnore).isEqualTo(expected);
        }
    }
}
