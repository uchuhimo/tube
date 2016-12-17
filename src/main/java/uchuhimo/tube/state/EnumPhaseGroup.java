package uchuhimo.tube.state;

import org.eclipse.collections.impl.factory.Maps;

import java.util.List;
import java.util.Map;

public interface EnumPhaseGroup<TState, TEnum extends Enum<TEnum>> extends PhaseGroup<TState> {

  static <TState, TEnum extends Enum<TEnum>> EnumPhaseGroup<TState, TEnum> of(
      Map<TEnum, PhaseRef<TState>> enumToPhase) {
    return new EnumPhaseGroup<TState, TEnum>() {
      private final List<PhaseRef<TState>> phases = Maps.immutable.ofAll(enumToPhase).toList();

      @Override
      public Map<TEnum, PhaseRef<TState>> getEnumToPhase() {
        return enumToPhase;
      }

      @Override
      public List<PhaseRef<TState>> getPhases() {
        return phases;
      }
    };
  }

  Map<TEnum, PhaseRef<TState>> getEnumToPhase();

  default PhaseRef<TState> getPhaseByEnum(TEnum phaseEnum) {
    return getEnumToPhase().get(phaseEnum);
  }
}
