package uchuhimo.tube.state;

import uchuhimo.tube.function.Function3;

import org.eclipse.collections.impl.factory.Lists;

import java.util.List;

public class Element3StateFactory<T1, T2, T3, TState> implements CompositeStateFactory<TState> {
  private final List<StateRef<?>> refs;
  private final Function3<T1, T2, T3, TState> stateGenerator;

  public Element3StateFactory(
      StateRef<T1> element1State,
      StateRef<T2> element2State,
      StateRef<T3> element3State,
      Function3<T1, T2, T3, TState> stateGenerator) {
    this.refs = Lists.fixedSize.of(element1State, element2State, element3State);
    this.stateGenerator = stateGenerator;
  }

  @Override
  public List<StateRef<?>> getRefs() {
    return refs;
  }

  @SuppressWarnings("unchecked")
  @Override
  public TState newStateWithRefs(Context context, List<?> refs) {
    return stateGenerator.apply((T1) refs.get(0), (T2) refs.get(1), (T3) refs.get(2));
  }
}
