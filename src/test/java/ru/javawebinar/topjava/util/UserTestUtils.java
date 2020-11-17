package ru.javawebinar.topjava.util;

import org.assertj.core.api.Assertions;
import ru.javawebinar.topjava.model.User;

import java.util.List;

import static ru.javawebinar.topjava.model.User.Role.ADMIN;
import static ru.javawebinar.topjava.model.User.Role.USER;

public class UserTestUtils {
    public static final Matcher USER_MATCHER = new Matcher();

    public static final int ADMIN_1_ID = 1;
    public static final User ADMIN_1 = new User(ADMIN_1_ID, "1_Admin", "admin", ADMIN);

    public static final int USER_2_ID = 2;
    public static final User USER_2 = new User(USER_2_ID, "2_User", "password", USER);

    public static final int USER_3_ID = 3;
    public static final User USER_3 = new User(USER_3_ID, "3_User", "120168", USER);

    public static final int NOT_FOUND_ID = 22;

    public static User getNew() {
        return new User("New", "secret", USER);
    }

    public static User getUpdated() {
        return new User(USER_2_ID, USER_2.getName(), "updatedpassword", ADMIN);
    }


    public static class Matcher {
        public void assertMatch(User actual, User expected) {
            Assertions.assertThat(actual).usingRecursiveComparison().ignoringFields("password").isEqualTo(expected);
        }

        public void assertMatch(List<User> actual, User... expected) {
            assertMatch(actual, List.of(expected));
        }

        public void assertMatch(List<User> actual, List<User> expected) {
            Assertions.assertThat(actual).usingElementComparatorIgnoringFields("password").isEqualTo(expected);
        }
    }
}
