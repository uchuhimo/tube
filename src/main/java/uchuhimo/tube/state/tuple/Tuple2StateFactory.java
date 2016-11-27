package uchuhimo.tube.state.tuple;

import uchuhimo.tube.state.CompositeStateFactory;
import uchuhimo.tube.state.StateRef;
import uchuhimo.tube.value.tuple.Tuple;
import uchuhimo.tube.value.tuple.Tuple2;

import org.eclipse.collections.impl.factory.Lists;

import java.util.List;

public class Tuple2StateFactory<T1, T2> implements CompositeStateFactory<Tuple2<T1, T2>> {
  private final List<StateRef<?>> refs;

  public Tuple2StateFactory(StateRef<T1> element1State, StateRef<T2> element2State) {
    this.refs = Lists.fixedSize.of(element1State, element2State);
  }

  @Override
  public List<StateRef<?>> getRefs() {
    return refs;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Tuple2<T1, T2> newStateWithRefs(Context context, List<?> refs) {
    return Tuple.of((T1) refs.get(0), (T2) refs.get(1));
  }
}
