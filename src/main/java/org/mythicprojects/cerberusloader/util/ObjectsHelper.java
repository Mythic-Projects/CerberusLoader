package org.mythicprojects.cerberusloader.util;

import java.util.Objects;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ObjectsHelper {

    private ObjectsHelper() {
    }

    public static <T> T requireNonNull(@Nullable T obj) {
        return Objects.requireNonNull(obj);
    }

    public static <T> T firstNonNull(@Nullable T first, @Nullable T second) {
        return first != null ? first : Objects.requireNonNull(second);
    }

    public static <U, T> @Nullable T safeNull(@Nullable U value, @NotNull Function<U, T> getter) {
        if (value == null) {
            return null;
        }
        return getter.apply(value);
    }

    public static <U, T> @Nullable T safeNull(@Nullable U first, @Nullable U second, @NotNull Function<U, T> getter) {
        T result = safeNull(first, getter);
        if (result != null) {
            return result;
        }
        return safeNull(second, getter);
    }

}
