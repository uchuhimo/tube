package uchuhimo.tube.state;

import com.google.common.base.Preconditions;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;

import java.util.Collection;

public class StateRefImpl<TState> implements StateRef<TState> {

  private final int id;
  private final MutableIntObjectMap<PhaseRef<TState>> phaseRegistry =
      IntObjectMaps.mutable.empty();
  private final InitContext<TState> context;
  private boolean borrowed = false;

  public StateRefImpl(InitContext<TState> context) {
    this.context = context;
    this.id = context.register(this);
  }

  @Override
  public int getStateId() {
    return id;
  }

  @Override
  public StateRepo getRepo() {
    return context.getRepo();
  }

  @Override
  public StateFactory<TState> getFactory() {
    return context.getFactory();
  }

  @Override
  public int getPartitionCount() {
    return context.getPartitionCount();
  }

  @Override
  public boolean isBorrowed() {
    return borrowed;
  }

  @Override
  public PhaseRepo<TState> borrow() {
    Preconditions.checkState(!borrowed);
    borrowed = true;

    return new PhaseRepo<TState>() {

      private int nextPhaseId = 0;

      @Override
      public StateRef<TState> getLenderStateRef() {
        return StateRefImpl.this;
      }

      @Override
      public PhaseRef<TState> newPhase(PhaseType phaseType) {

        return new PhaseRefImpl<>(new PhaseRefImpl.InitContext<TState>() {

          @Override
          public InitContext<TState> getStateRefContext() {
            return context;
          }

          @Override
          public int register(PhaseRef<TState> phaseRef) {
            final int phaseId = nextPhaseId;
            nextPhaseId++;
            phaseRegistry.put(phaseId, phaseRef);
            return phaseId;
          }

          @Override
          public PhaseType getType() {
            return phaseType;
          }
        });
      }
    };
  }

  @Override
  public Collection<PhaseRef<TState>> getPhases() {
    return phaseRegistry.values();
  }

  @Override
  public PhaseRef<TState> getPhaseById(int phaseId) {
    return phaseRegistry.get(phaseId);
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

    if (getStateId() != stateRef.getStateId()) {
      return false;
    }
    return getRepoId() == stateRef.getRepoId();
  }

  @Override
  public int hashCode() {
    int result = getStateId();
    result = 31 * result + getRepoId();
    return result;
  }

  public interface InitContext<TState> {

    StateRepo getRepo();

    int register(StateRef<TState> stateRef);

    StateFactory<TState> getFactory();

    int getPartitionCount();
  }
}
