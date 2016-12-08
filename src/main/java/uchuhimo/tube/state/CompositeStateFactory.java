package uchuhimo.tube.state;

import uchuhimo.tube.function.Function2;
import uchuhimo.tube.function.Function3;

import java.util.List;
import java.util.stream.Collectors;

public interface CompositeStateFactory<TState> extends StateFactory<TState> {
  static <T1, T2, TState> Element2StateFactory<T1, T2, TState> of(
      StateRef<T1> element1State,
      StateRef<T2> element2State,
      Function2<T1, T2, TState> stateGenerator) {
    return new Element2StateFactory<>(element1State, element2State, stateGenerator);
  }

  static <T1, T2, T3, TState> Element3StateFactory<T1, T2, T3, TState> of(
      StateRef<T1> element1State,
      StateRef<T2> element2State,
      StateRef<T3> element3State,
      Function3<T1, T2, T3, TState> stateGenerator) {
    return new Element3StateFactory<>(element1State, element2State, element3State, stateGenerator);
  }

  List<StateRef<?>> getRefs();

  TState newStateWithRefs(Context context, List<?> refs);

  @SuppressWarnings("unchecked")
  @Override
  default TState newState(Context context) {
    return newStateWithRefs(
        context,
        getRefs().stream().map(ref ->
            context.getState(((StateRef<Object>) ref))).collect(Collectors.toList()));
  }

  @SuppressWarnings("unchecked")
  @Override
  default void init(TState state, Context context) {
    getRefs().forEach(ref ->
        ((StateRef<Object>) ref).getFactory().init(context.getState(ref), context));
  }

  @SuppressWarnings("unchecked")
  @Override
  default void deinit(TState state, Context context) {
    getRefs().forEach(ref ->
        ((StateRef<Object>) ref).getFactory().deinit(context.getState(ref), context));
  }
}
