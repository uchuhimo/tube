package uchuhimo.tube.state;

import java.util.List;
import java.util.stream.Collectors;

public interface CompositeStateFactory<TState> extends StateFactory<TState> {
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
        ((StateRef<Object>) ref).getStateFactory().init(context.getState(ref), context));
  }

  @SuppressWarnings("unchecked")
  @Override
  default void deinit(TState state, Context context) {
    getRefs().forEach(ref ->
        ((StateRef<Object>) ref).getStateFactory().deinit(context.getState(ref), context));
  }
}
