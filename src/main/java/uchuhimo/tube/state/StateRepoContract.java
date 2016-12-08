package uchuhimo.tube.state;

import uchuhimo.tube.value.primitive.MutableDouble;
import uchuhimo.tube.value.primitive.MutableInt;
import uchuhimo.tube.value.primitive.MutableLong;
import uchuhimo.tube.value.tuple.Tuple;
import uchuhimo.tube.value.tuple.Tuple2;
import uchuhimo.tube.value.tuple.Tuple3;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import java.util.HashMap;
import java.util.Map;

public interface StateRepoContract extends StateRepo {
  static <TState> StateRef<TState> newStateRef(
      StateRepoContract repo,
      StateFactory<TState> factory,
      int partitionCount) {
    return new StateRef<TState>() {
      private final int id = repo.register(this);

      @Override
      public int getStateId() {
        return id;
      }

      @Override
      public StateRepo getRepo() {
        return repo;
      }

      @Override
      public StateFactory<TState> getFactory() {
        return factory;
      }

      @Override
      public int getPartitionCount() {
        return partitionCount;
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
    };
  }

  <TState> int register(StateRef<TState> stateRef);

  <TState> PhaseRepo<TState> registerPhaseGroup(StateRef<TState> stateRef);

  @Override
  default StateRef<MutableInt> newInt(int partitionCount) {
    return newStateRef(this, context -> new MutableInt(), partitionCount);
  }

  @Override
  default StateRef<MutableLong> newLong(int partitionCount) {
    return newStateRef(this, context -> new MutableLong(), partitionCount);
  }

  @Override
  default StateRef<MutableDouble> newDouble(int partitionCount) {
    return newStateRef(this, context -> new MutableDouble(), partitionCount);
  }

  @Override
  default <TKey, TValue> StateRef<Map<TKey, TValue>> newMap(int partitionCount) {
    return newStateRef(this, context -> new HashMap<>(), partitionCount);
  }

  @Override
  default <T1, T2> StateRef<Tuple2<T1, T2>> newTuple2(
      StateRef<T1> element1,
      StateRef<T2> element2,
      int partitionCount) {
    return newStateRef(
        this,
        CompositeStateFactory.of(element1, element2, Tuple::of),
        partitionCount);
  }

  @Override
  default <T1, T2, T3> StateRef<Tuple3<T1, T2, T3>> newTuple3(
      StateRef<T1> element1,
      StateRef<T2> element2,
      StateRef<T3> element3,
      int partitionCount) {
    return newStateRef(
        this,
        CompositeStateFactory.of(element1, element2, element3, Tuple::of),
        partitionCount);
  }

  @Override
  default <TState> StateRef<TState> newBy(StateFactory<TState> factory, int partitionCount) {
    return newStateRef(this, factory, partitionCount);
  }

  @Override
  default <TState> PhaseGroup<TState> newPhaseGroup(StateRef<TState> stateRef, int phaseCount) {
    final PhaseRepo<TState> phaseRepo = registerPhaseGroup(stateRef);
    final MutableList<PhaseRef<TState>> phases = Lists.mutable.empty();
    for (int i = 0; i < phaseCount; i++) {
      phases.add(phaseRepo.newWritable());
    }
    return PhaseGroup.of(phases);
  }
}
