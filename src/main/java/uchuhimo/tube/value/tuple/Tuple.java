package uchuhimo.tube.value.tuple;

public interface Tuple {
  static <T1, T2> Tuple2<T1, T2> of(T1 element1, T2 element2) {
    return Tuple2Impl.of(element1, element2);
  }

  static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 element1, T2 element2, T3 element3) {
    return Tuple3Impl.of(element1, element2, element3);
  }
}
