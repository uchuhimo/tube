package uchuhimo.tube.value.tuple;

import org.immutables.value.Value;

@Value.Immutable
@TupleStyle
public interface Tuple3<T1, T2, T3> extends Tuple {

  T1 field1();

  T2 field2();

  T3 field3();
}
