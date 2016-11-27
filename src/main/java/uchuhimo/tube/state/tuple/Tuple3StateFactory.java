package uchuhimo.tube.state.tuple;

import uchuhimo.tube.state.CompositeStateFactory;
import uchuhimo.tube.state.StateRef;
import uchuhimo.tube.value.tuple.Tuple;
import uchuhimo.tube.value.tuple.Tuple3;

import org.eclipse.collections.impl.factory.Lists;

import java.util.List;

public class Tuple3StateFactory<T1, T2, T3> implements CompositeStateFactory<Tuple3<T1, T2, T3>> {
  private final List<StateRef<?>> refs;

  public Tuple3StateFactory(
      StateRef<T1> element1State,
      StateRef<T2> element2State,
      StateRef<T3> element3State) {
    this.refs = Lists.fixedSize.of(element1State, element2State, element3State);
  }

  @Override
  public List<StateRef<?>> getRefs() {
    return refs;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Tuple3<T1, T2, T3> newStateWithRefs(Context context, List<?> refs) {
    return Tuple.of((T1) refs.get(0), (T2) refs.get(1), (T3) refs.get(2));
  }
}
