package am.dopomoga.aidtools.model;

import java.util.function.Consumer;
import java.util.function.Function;

public interface FunctionalModel<T> {

    default <U> U let(Function<T, U> function) {
        return function.apply((T) this);
    }

    default T apply(Consumer<T> consumer) {
        consumer.accept((T) this);
        return (T) this;
    }
}
