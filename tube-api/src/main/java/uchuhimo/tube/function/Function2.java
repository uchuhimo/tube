package uchuhimo.tube.function;

import java.io.Serializable;
import java.util.function.BiFunction;

@FunctionalInterface
public interface Function2<T1, T2, TResult> extends BiFunction<T1, T2, TResult>, Serializable {

  TResult apply(T1 argument1, T2 argument2);
}
