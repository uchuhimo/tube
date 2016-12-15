package uchuhimo.tube.state;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import java.io.Serializable;
import java.util.Collection;

public interface StateRef<TState> extends Serializable {

  int getStateId();

  StateRepo getRepo();

  default int getRepoId() {
    return getRepo().getId();
  }

  StateFactory<TState> getFactory();

  int getPartitionCount();

  boolean isBorrowed();

  PhaseRepo<TState> borrow();

  Collection<PhaseRef<TState>> getPhases();

  PhaseRef<TState> getPhaseById(int phaseId);

  default PhaseGroup phaseBy(int phaseCount) {
    final PhaseRepo<TState> phaseRepo = borrow();
    final MutableList<PhaseRef<TState>> phases = Lists.mutable.empty();
    for (int i = 0; i < phaseCount; i++) {
      phases.add(phaseRepo.newWritable());
    }
    return PhaseGroup.of(phases);
  }
}
