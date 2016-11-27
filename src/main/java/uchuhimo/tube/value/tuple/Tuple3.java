package uchuhimo.tube.value.tuple;

import org.immutables.value.Value;

@Value.Immutable
@TupleStyle
public interface Tuple3<T1, T2, T3> extends Tuple {
  T1 getElement1();

  T2 getElement2();

  T3 getElement3();
}
