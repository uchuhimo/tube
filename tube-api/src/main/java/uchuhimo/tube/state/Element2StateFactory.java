package uchuhimo.tube.state;

import uchuhimo.tube.function.Function2;

import org.eclipse.collections.impl.factory.Lists;

import java.util.List;

public class Element2StateFactory<T1, T2, TState> implements CompositeStateFactory<TState> {

  private final List<StateRef<?>> refs;
  private final Function2<T1, T2, TState> stateGenerator;

  public Element2StateFactory(
      StateRef<T1> element1State,
      StateRef<T2> element2State,
      Function2<T1, T2, TState> stateGenerator) {
    this.refs = Lists.fixedSize.of(element1State, element2State);
    this.stateGenerator = stateGenerator;
  }

  @Override
  public List<StateRef<?>> getRefs() {
    return refs;
  }

  @SuppressWarnings("unchecked")
  @Override
  public TState newStateWithRefs(Context context, List<?> refs) {
    return stateGenerator.apply((T1) refs.get(0), (T2) refs.get(1));
  }
}
