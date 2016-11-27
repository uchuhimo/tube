package uchuhimo.tube;

import uchuhimo.tube.state.StateAllocator;
import uchuhimo.tube.state.StateRef;

import java.io.Serializable;

public interface TubeSession extends StateAllocator, Serializable {
  static TubeSession newInstance() {
    return TubeSessionImpl.newInstance();
  }

  int getId();

  <TState> int registerState(StateRef<TState> stateRef);

  <TState> StateRef<TState> getStateById(int id);

  default <TState> StateRef<TState> getStateById(int id, Class<TState> clazz) {
    return getStateById(id);
  }

  @Override
  default TubeSession getSession() {
    return this;
  }
}
