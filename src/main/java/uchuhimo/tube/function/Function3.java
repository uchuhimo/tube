package uchuhimo.tube.function;

import java.io.Serializable;

@FunctionalInterface
public interface Function3<T1, T2, T3, TResult> extends Serializable {
  TResult apply(T1 argument1, T2 argument2, T3 argument3);
}
