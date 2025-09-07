package org.common.utils;




import java.util.NoSuchElementException;
import java.util.Optional;

public class OptionalUtil {
    public static <T> T getOrElseThrow(final Optional<T> optional, final String message) {
        return optional.orElseThrow(() -> new NoSuchElementException(message));
    }
}
