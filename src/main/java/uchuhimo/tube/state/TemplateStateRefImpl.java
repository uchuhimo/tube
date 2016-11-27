package uchuhimo.tube.state;

import uchuhimo.tube.TubeSession;

public class TemplateStateRefImpl<TState> implements StateRef<TState> {
  private final int id;
  private final int sessionId;
  private final StateFactory<TState> stateFactory;

  public TemplateStateRefImpl(TubeSession session, StateFactory<TState> stateFactory) {
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
}
