package uchuhimo.tube.state;

import uchuhimo.tube.value.primitive.MutableDouble;
import uchuhimo.tube.value.primitive.MutableInt;
import uchuhimo.tube.value.primitive.MutableLong;
import uchuhimo.tube.value.tuple.Tuple;
import uchuhimo.tube.value.tuple.Tuple2;
import uchuhimo.tube.value.tuple.Tuple3;

import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StateRepoImpl implements StateRepo {

  private final MutableIntObjectMap<StateRef<?>> stateRegistry = IntObjectMaps.mutable.empty();
  private final Set<StateRef<?>> rootRefs = Sets.mutable.empty();
  private final InitContext context;
  private final int id;
  private int nextStateId = 0;

  public StateRepoImpl(InitContext context) {
    this.context = context;
    this.id = context.register(this);
  }

  <TState> StateRef<TState> newStateRef(
      StateRepoImpl repo,
      StateFactory<TState> factory,
      int partitionCount) {

    return new StateRefImpl<>(new StateRefImpl.InitContext<TState>() {
      @Override
      public StateRepo getRepo() {
        return repo;
      }

      @Override
      public int register(StateRef<TState> stateRef) {
        final int stateId = nextStateId;
        nextStateId++;
        stateRegistry.put(stateId, stateRef);
        if (!(stateRef instanceof PhaseRef)) {
          rootRefs.add(stateRef);
        }
        return stateId;
      }

      @Override
      public StateFactory<TState> getFactory() {
        return factory;
      }

      @Override
      public int getPartitionCount() {
        return partitionCount;
      }
    });
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public Collection<StateRef<?>> getRootRefs() {
    return rootRefs;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <TState> StateRef<TState> getStateRefById(int id) {
    return (StateRef<TState>) stateRegistry.get(id);
  }

  @Override
  public int getDefaultPartitionCount() {
    return context.getDefaultPartitionCount();
  }

  @Override
  public StateRef<MutableInt> newInt(int partitionCount) {
    return newStateRef(this, context -> new MutableInt(), partitionCount);
  }

  @Override
  public StateRef<MutableLong> newLong(int partitionCount) {
    return newStateRef(this, context -> new MutableLong(), partitionCount);
  }

  @Override
  public StateRef<MutableDouble> newDouble(int partitionCount) {
    return newStateRef(this, context -> new MutableDouble(), partitionCount);
  }

  @Override
  public <TKey, TValue> StateRef<Map<TKey, TValue>> newMap(int partitionCount) {
    return newStateRef(this, context -> new HashMap<>(), partitionCount);
  }

  @Override
  public <T1, T2> StateRef<Tuple2<T1, T2>> newTuple2(
      StateRef<T1> element1,
      StateRef<T2> element2,
      int partitionCount) {

    return newStateRef(
        this,
        CompositeStateFactory.of(element1, element2, Tuple::of),
        partitionCount);
  }

  @Override
  public <T1, T2, T3> StateRef<Tuple3<T1, T2, T3>> newTuple3(
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
  public <TState> StateRef<TState> newBy(StateFactory<TState> factory, int partitionCount) {
    return newStateRef(this, factory, partitionCount);
  }

  public interface InitContext {

    int register(StateRepo stateRepo);

    int getDefaultPartitionCount();
  }
}
