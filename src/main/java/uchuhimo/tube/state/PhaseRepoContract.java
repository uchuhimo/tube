package uchuhimo.tube.state;

import org.eclipse.collections.api.tuple.primitive.IntIntPair;

public interface PhaseRepoContract<TState> extends PhaseRepo<TState> {

  static <TState> PhaseRef<TState> newPhaseRef(
      PhaseRepoContract<TState> phaseRepo,
      PhaseType phaseType) {

    return new PhaseRef<TState>() {

      private final IntIntPair stateIdAndPhaseId = phaseRepo.register(this);

      @Override
      public int getPhaseId() {
        return stateIdAndPhaseId.getTwo();
      }

      @Override
      public PhaseType getPhaseType() {
        return phaseType;
      }

      @Override
      public int getStateId() {
        return stateIdAndPhaseId.getOne();
      }

      @Override
      public StateRepo getRepo() {
        return phaseRepo.getLenderStateRef().getRepo();
      }

      @Override
      public StateFactory<TState> getFactory() {
        return phaseRepo.getLenderStateRef().getFactory();
      }

      @Override
      public int getPartitionCount() {
        return phaseRepo.getLenderStateRef().getPartitionCount();
      }
    };
  }

  <TState> IntIntPair register(PhaseRef<TState> phaseRef);

  @Override
  default PhaseRef<TState> newWritable() {
    return newPhaseRef(this, PhaseType.Writable);
  }

  @Override
  default PhaseRef<TState> newReadOnly() {
    return newPhaseRef(this, PhaseType.ReadOnly);
  }

  @Override
  default PhaseRef<TState> newBroadcast() {
    return newPhaseRef(this, PhaseType.Broadcast);
  }
}
