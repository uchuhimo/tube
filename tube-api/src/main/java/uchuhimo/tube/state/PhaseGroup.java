package uchuhimo.tube.state;

import java.util.Collections;
import java.util.List;

public interface PhaseGroup<TState> {

  static <TState> PhaseGroup<TState> of(List<PhaseRef<TState>> phases) {
    return new PhaseGroup<TState>() {
      @Override
      public List<PhaseRef<TState>> getPhases() {
        return Collections.unmodifiableList(phases);
      }
    };
  }

  List<PhaseRef<TState>> getPhases();
}
