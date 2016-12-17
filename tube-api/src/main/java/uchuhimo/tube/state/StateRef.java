package uchuhimo.tube.state;

import uchuhimo.tube.value.tuple.Tuple;
import uchuhimo.tube.value.tuple.Tuple2;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.utility.ArrayIterate;

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

  default StateRef<TState> copy() {
    return getRepo().newBy(getFactory(), getPartitionCount());
  }

  default StateRef<TState> withPartitionCount(int partitionCount) {
    return getRepo().newBy(getFactory(), partitionCount);
  }

  default PhaseGroup<TState> phaseBy(int phaseCount) {
    final PhaseRepo<TState> phaseRepo = borrow();
    final MutableList<PhaseRef<TState>> phases = Lists.mutable.empty();
    for (int i = 0; i < phaseCount; i++) {
      phases.add(phaseRepo.newWritable());
    }
    return PhaseGroup.of(phases);
  }

  default <TEnum extends Enum<TEnum> & PhaseEnum> EnumPhaseGroup<TState, TEnum> phaseBy(
      Class<TEnum> enumClass) {
    return phaseBy(enumClass.getEnumConstants());
  }

  default <TEnum extends Enum<TEnum> & PhaseEnum> EnumPhaseGroup<TState, TEnum> phaseBy(
      TEnum[] phaseEnums) {
    return phaseBy(
        ArrayIterate.collect(phaseEnums, phaseEnum -> Tuple.of(phaseEnum, phaseEnum.getType())));
  }

  default <TEnum extends Enum<TEnum>> EnumPhaseGroup<TState, TEnum> phaseBy(
      Iterable<Tuple2<TEnum, PhaseType>> enumToPhaseType) {
    final PhaseRepo<TState> phaseRepo = borrow();
    final MutableMap<TEnum, PhaseRef<TState>> enumToPhase = Maps.mutable.empty();
    for (Tuple2<TEnum, PhaseType> enumWithPhaseType : enumToPhaseType) {
      enumToPhase.put(enumWithPhaseType.field1(), phaseRepo.newPhase(enumWithPhaseType.field2()));
    }
    return EnumPhaseGroup.of(enumToPhase);
  }
}
