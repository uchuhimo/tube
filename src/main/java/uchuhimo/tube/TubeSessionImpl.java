package uchuhimo.tube;

import uchuhimo.tube.state.StateRef;

import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;

import java.util.concurrent.atomic.AtomicInteger;

public class TubeSessionImpl implements InternalTubeSession {
  private static final AtomicInteger sessionIdGenerator = new AtomicInteger(0);
  private final int id;
  private final AtomicInteger stateIdGenerator = new AtomicInteger(0);
  private final MutableIntObjectMap<StateRef<?>> stateRegistry = IntObjectMaps.mutable.empty();

  public TubeSessionImpl(int id) {
    this.id = id;
  }

  public static TubeSession newInstance() {
    return new TubeSessionImpl(sessionIdGenerator.getAndIncrement());
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public MutableIntObjectMap<StateRef<?>> getStateRegistry() {
    return stateRegistry;
  }

  @Override
  public int nextStateId() {
    return stateIdGenerator.getAndIncrement();
  }
}
