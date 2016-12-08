package uchuhimo.tube.state;

public interface PhaseRef<TState> extends StateRef<TState> {

  int getPhaseId();

  PhaseType getPhaseType();
}
