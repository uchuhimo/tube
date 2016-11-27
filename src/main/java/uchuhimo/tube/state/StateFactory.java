package uchuhimo.tube.state;

import java.io.Serializable;

public interface StateFactory<TState> extends Serializable {
  TState newState(Context context);

  default void init(TState state) {
  }

  default void deinit(TState state) {
  }

  interface Context {
    int getPartitionId();
  }
}
