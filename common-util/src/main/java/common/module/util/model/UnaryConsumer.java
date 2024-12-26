package common.module.util.model;

@FunctionalInterface
public interface UnaryConsumer<T> {

    void consume(T a, T b);

}
