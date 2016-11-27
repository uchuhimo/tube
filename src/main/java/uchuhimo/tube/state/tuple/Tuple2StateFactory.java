package uchuhimo.tube.state.tuple;

import uchuhimo.tube.state.StateFactory;
import uchuhimo.tube.value.tuple.Tuple;
import uchuhimo.tube.value.tuple.Tuple2;

public class Tuple2StateFactory<T1, T2> implements StateFactory<Tuple2<T1, T2>> {
  private final StateFactory<T1> element1Factory;
  private final StateFactory<T2> element2Factory;

  public Tuple2StateFactory(StateFactory<T1> element1Factory, StateFactory<T2> element2Factory) {
    this.element1Factory = element1Factory;
    this.element2Factory = element2Factory;
  }

  @Override
  public Tuple2<T1, T2> newState(Context context) {
    return Tuple.of(element1Factory.newState(context), element2Factory.newState(context));
  }

  @Override
  public void init(Tuple2<T1, T2> tuple) {
    element1Factory.init(tuple.getElement1());
    element2Factory.init(tuple.getElement2());
  }

  @Override
  public void deinit(Tuple2<T1, T2> tuple) {
    element1Factory.deinit(tuple.getElement1());
    element2Factory.deinit(tuple.getElement2());
  }
}
