package io.github.lanicc.zookits.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Created on 2022/7/14.
 *
 * @author lan
 */
public final class Predicates {

    public static void notBlank(String s) {
        test(s, StringUtils::isNotBlank);
    }

    public static <T> void notNull(T t) {
        test(t, Objects::nonNull);
    }

    public static <T> void test(T t, Predicate<T> predicate) {
        assert predicate.test(t);
    }
}
