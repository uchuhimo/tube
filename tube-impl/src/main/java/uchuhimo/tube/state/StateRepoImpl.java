package uchuhimo.tube.state;

import uchuhimo.tube.value.primitive.MutableDouble;
import uchuhimo.tube.value.primitive.MutableInt;
import uchuhimo.tube.value.primitive.MutableLong;
import uchuhimo.tube.value.primitive.MutableString;
import uchuhimo.tube.value.tuple.Tuple;
import uchuhimo.tube.value.tuple.Tuple2;
import uchuhimo.tube.value.tuple.Tuple3;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.eclipse.collections.impl.set.mutable.SetAdapter;

import java.util.Collection;
import java.util.Collections;
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

  private <TState> StateRef<TState> newStateRef(StateFactory<TState> factory, int partitionCount) {

    return new StateRefImpl<>(new StateRefImpl.InitContext<TState>() {

      @Override
      public StateRepo getRepo() {
        return StateRepoImpl.this;
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
    return Collections.unmodifiableCollection(rootRefs);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <TState> StateRef<TState> getStateRef(int id) {
    return (StateRef<TState>) stateRegistry.get(id);
  }

  @Override
  public int getDefaultPartitionCount() {
    return context.getDefaultPartitionCount();
  }

  @Override
  public StateRef<MutableInt> newInt(int partitionCount) {
    return newStateRef(context -> new MutableInt(), partitionCount);
  }

  @Override
  public StateRef<MutableLong> newLong(int partitionCount) {
    return newStateRef(context -> new MutableLong(), partitionCount);
  }

  @Override
  public StateRef<MutableDouble> newDouble(int partitionCount) {
    return newStateRef(context -> new MutableDouble(), partitionCount);
  }

  @Override
  public StateRef<MutableString> newString(int partitionCount) {
    return newStateRef(context -> new MutableString(), partitionCount);
  }

  @Override
  public <TKey, TValue> StateRef<Map<TKey, TValue>> newMap(int partitionCount) {
    return newStateRef(context -> new HashMap<>(), partitionCount);
  }

  @Override
  public <T1, T2> StateRef<Tuple2<T1, T2>> newTuple2(
      StateRef<T1> element1,
      StateRef<T2> element2,
      int partitionCount) {

    return newStateRef(
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
        CompositeStateFactory.of(element1, element2, element3, Tuple::of),
        partitionCount);
  }

  @Override
  public <TState> StateRef<TState> newBy(StateFactory<TState> factory, int partitionCount) {
    return newStateRef(factory, partitionCount);
  }

  @Override
  public String dumpRefs() {
    final StringBuilder output = new StringBuilder();
    final MutableSortedSet<StateRef<?>> sortedRootRefs =
        SetAdapter.adapt(rootRefs).toSortedSetBy(StateRef::getStateId);
    int restRootRefCount = sortedRootRefs.size();
    for (StateRef<?> rootRef : sortedRootRefs) {
      restRootRefCount--;
      if (restRootRefCount == 0) {
        dumpRefsInRecursion(rootRef, output, true, Lists.immutable.empty());
      } else {
        dumpRefsInRecursion(rootRef, output, false, Lists.immutable.empty());
      }
    }
    return output.toString();
  }

  private <TState> void dumpRefsInRecursion(
      StateRef<TState> ref,
      StringBuilder output,
      boolean isLastChild,
      ImmutableList<Boolean> levelToIsLastChild) {
    for (Boolean isLastChildInCurrentLevel : levelToIsLastChild) {
      if (isLastChildInCurrentLevel) {
        output.append("     ");
      } else {
        output.append("|    ");
      }
    }
    if (isLastChild) {
      output.append("\\--- ");
    } else {
      output.append("+--- ");
    }
    output.append(ref.toString()).append("\n");
    if (ref.isBorrowed()) {
      int restChildCount = ref.getPhases().size();
      final ImmutableList<Boolean> newLevelToIsLastChild = levelToIsLastChild.newWith(isLastChild);
      for (StateRef<TState> childRef : ref.getPhases()) {
        restChildCount--;
        if (restChildCount == 0) {
          dumpRefsInRecursion(childRef, output, true, newLevelToIsLastChild);
        } else {
          dumpRefsInRecursion(childRef, output, false, newLevelToIsLastChild);
        }
      }
    }
  }

  public interface InitContext {

    int register(StateRepo stateRepo);

    int getDefaultPartitionCount();
  }
}
