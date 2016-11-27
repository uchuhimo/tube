package uchuhimo.tube.state;

import java.io.Serializable;

public interface StateRef<TState> extends Serializable {
  int getId();

  int getSessionId();

  StateFactory<TState> getStateFactory();
}
