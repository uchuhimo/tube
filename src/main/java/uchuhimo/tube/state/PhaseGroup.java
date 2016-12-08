package uchuhimo.tube.state;

import java.util.List;

public interface PhaseGroup<TState> {
  List<PhaseRef<TState>> getPhases();

  static <TState> PhaseGroup<TState> of(List<PhaseRef<TState>> phases) {
    return new PhaseGroup<TState>() {
      @Override
      public List<PhaseRef<TState>> getPhases() {
        return phases;
      }
    };
  }
}
