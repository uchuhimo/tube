package uchuhimo.tube.state;

import uchuhimo.tube.TubeSession;

public class StateRefImpl<TState> implements StateRef<TState> {
  private final int id;
  private final int sessionId;
  private final StateFactory<TState> stateFactory;

  public StateRefImpl(TubeSession session, StateFactory<TState> stateFactory) {
    this.id = session.registerState(this);
    this.sessionId = session.getId();
    this.stateFactory = stateFactory;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public int getSessionId() {
    return sessionId;
  }

  @Override
  public StateFactory<TState> getStateFactory() {
    return stateFactory;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }

    StateRef<?> stateRef = (StateRef<?>) other;

    if (getId() != stateRef.getId()) {
      return false;
    }
    return getSessionId() == stateRef.getSessionId();
  }

  @Override
  public int hashCode() {
    int result = getId();
    result = 31 * result + getSessionId();
    return result;
  }
}
