package uchuhimo.tube.state;

public interface PhaseRepo<TState> {

  StateRef<TState> getLenderStateRef();

  PhaseRef<TState> newWritable();

  PhaseRef<TState> newReadOnly();

  PhaseRef<TState> newBroadcast();

  default PhaseRef<TState> newPhase() {
    return newWritable();
  }
}
