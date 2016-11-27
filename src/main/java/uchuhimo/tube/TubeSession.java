package uchuhimo.tube;

import uchuhimo.tube.state.StateAllocator;
import uchuhimo.tube.state.StateRef;
import uchuhimo.tube.state.StateRefContainer;

import java.io.Serializable;

public interface TubeSession extends StateAllocator, StateRefContainer, Serializable {
  static TubeSession newInstance() {
    return TubeSessionImpl.newInstance();
  }

  int getId();

  <TState> int registerState(StateRef<TState> stateRef);

  @Override
  default TubeSession getSession() {
    return this;
  }
}
