package uchuhimo.tube.state;

import java.io.Serializable;

public interface StateRef<TState> extends Serializable {

  int getStateId();

  StateRepo getRepo();

  default int getRepoId() {
    return getRepo().getId();
  }

  StateFactory<TState> getFactory();

  int getPartitionCount();

  default PhaseGroup phaseBy(int phaseCount) {
    return getRepo().newPhaseGroup(this, phaseCount);
  }
}
