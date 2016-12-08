package uchuhimo.tube.state;

import java.io.Serializable;

public interface StateFactory<TState> extends Serializable {

  TState newState(Context context);

  default void init(TState state, Context context) {
  }

  default void deinit(TState state, Context context) {
  }

  interface Context {

    int getPartitionId();

    <TState> TState getState(StateRef<TState> ref);
  }
}
