package uchuhimo.tube.state.tuple;

import uchuhimo.tube.state.StateFactory;
import uchuhimo.tube.value.tuple.Tuple;
import uchuhimo.tube.value.tuple.Tuple3;

public class Tuple3StateFactory<T1, T2, T3> implements StateFactory<Tuple3<T1, T2, T3>> {
  private final StateFactory<T1> element1Factory;
  private final StateFactory<T2> element2Factory;
  private final StateFactory<T3> element3Factory;

  public Tuple3StateFactory(
      StateFactory<T1> element1Factory,
      StateFactory<T2> element2Factory,
      StateFactory<T3> element3Factory) {
    this.element1Factory = element1Factory;
    this.element2Factory = element2Factory;
    this.element3Factory = element3Factory;
  }

  @Override
  public Tuple3<T1, T2, T3> newState(Context context) {
    return Tuple.of(
        element1Factory.newState(context),
        element2Factory.newState(context),
        element3Factory.newState(context));
  }

  @Override
  public void init(Tuple3<T1, T2, T3> tuple) {
    element1Factory.init(tuple.getElement1());
    element2Factory.init(tuple.getElement2());
    element3Factory.init(tuple.getElement3());
  }

  @Override
  public void deinit(Tuple3<T1, T2, T3> tuple) {
    element1Factory.deinit(tuple.getElement1());
    element2Factory.deinit(tuple.getElement2());
    element3Factory.deinit(tuple.getElement3());
  }
}
