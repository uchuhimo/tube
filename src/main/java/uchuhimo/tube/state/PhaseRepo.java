package uchuhimo.tube.state;

public interface PhaseRepo<TState> {

  StateRef<TState> getLenderStateRef();

  PhaseRef<TState> newPhase(PhaseType phaseType);

  default PhaseRef<TState> newPhase() {
    return newWritable();
  }

  default PhaseRef<TState> newWritable() {
    return newPhase(PhaseType.Writable);
  }

  default PhaseRef<TState> newReadOnly() {
    return newPhase(PhaseType.ReadOnly);
  }

  default PhaseRef<TState> newBroadcast() {
    return newPhase(PhaseType.Broadcast);
  }
}
