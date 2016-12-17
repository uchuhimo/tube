package uchuhimo.tube.function;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface Function1<T1, TResult> extends Function<T1, TResult>, Serializable {

  TResult apply(T1 argument1);
}
