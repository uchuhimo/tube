package uchuhimo.tube.state;

import uchuhimo.tube.TubeRuntimeException;
import uchuhimo.tube.TubeSession;
import uchuhimo.tube.function.Function0;
import uchuhimo.tube.state.tuple.Tuple2StateFactory;
import uchuhimo.tube.state.tuple.Tuple3StateFactory;
import uchuhimo.tube.value.primitive.MutableDouble;
import uchuhimo.tube.value.primitive.MutableInt;
import uchuhimo.tube.value.primitive.MutableLong;
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
    return new StateRefImpl<>(getSession(), new Tuple2StateFactory<>(element1, element2));
  }

  default <T1, T2, T3> StateRef<Tuple3<T1, T2, T3>> newTuple3(
      StateRef<T1> element1,
      StateRef<T2> element2,
      StateRef<T3> element3) {
    return new StateRefImpl<>(
        getSession(),
        new Tuple3StateFactory<>(element1, element2, element3));
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
}
