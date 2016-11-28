package uchuhimo.tube.state;

import uchuhimo.tube.TubeRuntimeException;
import uchuhimo.tube.TubeSession;
import uchuhimo.tube.function.Function0;
import uchuhimo.tube.function.Function2;
import uchuhimo.tube.function.Function3;
import uchuhimo.tube.value.primitive.MutableDouble;
import uchuhimo.tube.value.primitive.MutableInt;
import uchuhimo.tube.value.primitive.MutableLong;
import uchuhimo.tube.value.tuple.Tuple;
import uchuhimo.tube.value.tuple.Tuple2;
import uchuhimo.tube.value.tuple.Tuple3;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public interface StateAllocator extends Serializable {
  TubeSession getSession();

  default StateRef<MutableInt> newInt() {
    return new StateRefImpl<>(getSession(), context -> new MutableInt());
  }

  default StateRef<MutableLong> newLong() {
    return new StateRefImpl<>(getSession(), context -> new MutableLong());
  }

  default StateRef<MutableDouble> newDouble() {
    return new StateRefImpl<>(getSession(), context -> new MutableDouble());
  }

  default <TKey, TValue> StateRef<Map<TKey, TValue>> newMap() {
    return new StateRefImpl<>(getSession(), new StateFactory<Map<TKey, TValue>>() {
      @Override
      public Map<TKey, TValue> newState(Context context) {
        return new HashMap<>();
      }

      @Override
      public void deinit(Map<TKey, TValue> map, Context context) {
        map.clear();
      }
    });
  }

  default <TKey, TValue> StateRef<Map<TKey, TValue>> newMap(Class<TKey> keyClass, Class<TValue> valueClass) {
    return newMap();
  }

  default <T1, T2> StateRef<Tuple2<T1, T2>> newTuple2(StateRef<T1> element1, StateRef<T2> element2) {
    return new StateRefImpl<>(
        getSession(),
        CompositeStateFactory.of(element1, element2, Tuple::of));
  }

  default <T1, T2, T3> StateRef<Tuple3<T1, T2, T3>> newTuple3(
      StateRef<T1> element1,
      StateRef<T2> element2,
      StateRef<T3> element3) {
    return new StateRefImpl<>(
        getSession(),
        CompositeStateFactory.of(element1, element2, element3, Tuple::of));
  }

  default <TState> StateRef<TState> newBy(StateFactory<TState> factory) {
    return new StateRefImpl<>(getSession(), factory);
  }

  default <TState> StateRef<TState> newBy(Function0<TState> contextFreeFactory) {
    return newBy(context -> contextFreeFactory.apply());
  }

  default <TState> StateRef<TState> newBy(Class<TState> clazz) {
    return newBy(context -> {
      try {
        return clazz.newInstance();
      } catch (InstantiationException | IllegalAccessException exception) {
        throw new TubeRuntimeException("fail to create state from class", exception);
      }
    });
  }

  default <T1, T2, TState> StateRef<TState> newComposite(
      StateRef<T1> element1State,
      StateRef<T2> element2State,
      Function2<T1, T2, TState> stateGenerator) {
    return newBy(new Element2StateFactory<>(element1State, element2State, stateGenerator));
  }

  default <T1, T2, T3, TState> StateRef<TState> newComposite(
      StateRef<T1> element1State,
      StateRef<T2> element2State,
      StateRef<T3> element3State,
      Function3<T1, T2, T3, TState> stateGenerator) {
    return newBy(
        new Element3StateFactory<>(element1State, element2State, element3State, stateGenerator));
  }
}
