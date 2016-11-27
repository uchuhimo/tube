package uchuhimo.tube;

import uchuhimo.tube.state.StateRef;

import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;

public interface InternalTubeSession extends TubeSession {
  MutableIntObjectMap<StateRef<?>> getStateRegistry();

  int nextStateId();

  @Override
  default <TState> int registerState(StateRef<TState> stateRef) {
    final int stateId = nextStateId();
    getStateRegistry().put(stateId, stateRef);
    return stateId;
  }

  @SuppressWarnings("unchecked")
  @Override
  default <TState> StateRef<TState> getStateById(int id) {
    return (StateRef<TState>) getStateRegistry().get(id);
  }
}
