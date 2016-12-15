package uchuhimo.tube.state;

import uchuhimo.tube.TubeRuntimeException;
import uchuhimo.tube.function.Function0;
import uchuhimo.tube.function.Function2;
import uchuhimo.tube.function.Function3;
import uchuhimo.tube.value.primitive.MutableDouble;
import uchuhimo.tube.value.primitive.MutableInt;
import uchuhimo.tube.value.primitive.MutableLong;
import uchuhimo.tube.value.tuple.Tuple2;
import uchuhimo.tube.value.tuple.Tuple3;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public interface StateRepo extends Serializable {

  int getId();

  Collection<StateRef<?>> getRootRefs();

  <TState> StateRef<TState> getStateRefById(int id);

  default <TState> StateRef<TState> getStateRefById(int id, Class<TState> clazz) {
    return getStateRefById(id);
  }

  int getDefaultPartitionCount();

  StateRef<MutableInt> newInt(int partitionCount);

  default StateRef<MutableInt> newInt() {
    return newInt(getDefaultPartitionCount());
  }

  StateRef<MutableLong> newLong(int partitionCount);

  default StateRef<MutableLong> newLong() {
    return newLong(getDefaultPartitionCount());
  }

  StateRef<MutableDouble> newDouble(int partitionCount);

  default StateRef<MutableDouble> newDouble() {
    return newDouble(getDefaultPartitionCount());
  }

  <TKey, TValue> StateRef<Map<TKey, TValue>> newMap(int partitionCount);

  default <TKey, TValue> StateRef<Map<TKey, TValue>> newMap() {
    return newMap(getDefaultPartitionCount());
  }

  default <TKey, TValue> StateRef<Map<TKey, TValue>> newMap(
      Class<TKey> keyClass,
      Class<TValue> valueClass) {

    return newMap(keyClass, valueClass, getDefaultPartitionCount());
  }

  default <TKey, TValue> StateRef<Map<TKey, TValue>> newMap(
      Class<TKey> keyClass,
      Class<TValue> valueClass,
      int partitionCount) {

    return newMap(partitionCount);
  }

  <T1, T2> StateRef<Tuple2<T1, T2>> newTuple2(
      StateRef<T1> element1,
      StateRef<T2> element2,
      int partitionCount);

  default <T1, T2> StateRef<Tuple2<T1, T2>> newTuple2(
      StateRef<T1> element1,
      StateRef<T2> element2) {

    return newTuple2(element1, element2, getDefaultPartitionCount());
  }

  <T1, T2, T3> StateRef<Tuple3<T1, T2, T3>> newTuple3(
      StateRef<T1> element1,
      StateRef<T2> element2,
      StateRef<T3> element3,
      int partitionCount);

  default <T1, T2, T3> StateRef<Tuple3<T1, T2, T3>> newTuple3(
      StateRef<T1> element1,
      StateRef<T2> element2,
      StateRef<T3> element3) {

    return newTuple3(element1, element2, element3, getDefaultPartitionCount());
  }

  <TState> StateRef<TState> newBy(StateFactory<TState> factory, int partitionCount);

  default <TState> StateRef<TState> newBy(StateFactory<TState> factory) {
    return newBy(factory, getDefaultPartitionCount());
  }

  default <TState> StateRef<TState> newBy(Function0<TState> contextFreeFactory) {
    return newBy(contextFreeFactory, getDefaultPartitionCount());
  }

  default <TState> StateRef<TState> newBy(Function0<TState> contextFreeFactory, int partitionCount) {
    return newBy(context -> contextFreeFactory.apply(), partitionCount);
  }

  default <TState> StateRef<TState> newBy(Class<TState> clazz) {
    return newBy(clazz, getDefaultPartitionCount());
  }

  default <TState> StateRef<TState> newBy(Class<TState> clazz, int partitionCount) {
    return newBy(
        context -> {
          try {
            return clazz.newInstance();
          } catch (InstantiationException | IllegalAccessException exception) {
            throw new TubeRuntimeException("fail to create state from class", exception);
          }
        },
        partitionCount);
  }

  default <T1, T2, TState> StateRef<TState> newComposite(
      StateRef<T1> element1State,
      StateRef<T2> element2State,
      Function2<T1, T2, TState> stateGenerator) {

    return newComposite(element1State, element2State, stateGenerator, getDefaultPartitionCount());
  }

  default <T1, T2, TState> StateRef<TState> newComposite(
      StateRef<T1> element1State,
      StateRef<T2> element2State,
      Function2<T1, T2, TState> stateGenerator,
      int partitionCount) {

    return newBy(
        new Element2StateFactory<>(element1State, element2State, stateGenerator),
        partitionCount);
  }

  default <T1, T2, T3, TState> StateRef<TState> newComposite(
      StateRef<T1> element1State,
      StateRef<T2> element2State,
      StateRef<T3> element3State,
      Function3<T1, T2, T3, TState> stateGenerator) {

    return newComposite(
        element1State,
        element2State,
        element3State,
        stateGenerator,
        getDefaultPartitionCount());
  }

  default <T1, T2, T3, TState> StateRef<TState> newComposite(
      StateRef<T1> element1State,
      StateRef<T2> element2State,
      StateRef<T3> element3State,
      Function3<T1, T2, T3, TState> stateGenerator,
      int partitionCount) {

    return newBy(
        new Element3StateFactory<>(element1State, element2State, element3State, stateGenerator),
        partitionCount);
  }
}
