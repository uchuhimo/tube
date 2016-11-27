package uchuhimo.tube.value.tuple;

import org.immutables.value.Value;

@Value.Immutable
@TupleStyle
public interface Tuple2<T1, T2> extends Tuple {
  T1 getElement1();

  T2 getElement2();
}
