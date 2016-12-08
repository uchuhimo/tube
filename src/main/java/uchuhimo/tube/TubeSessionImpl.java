package uchuhimo.tube;

import uchuhimo.tube.state.PhaseRef;
import uchuhimo.tube.state.PhaseRepo;
import uchuhimo.tube.state.PhaseRepoContract;
import uchuhimo.tube.state.StateRef;
import uchuhimo.tube.state.StateRepoContract;

import com.google.common.base.Preconditions;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;

import java.util.concurrent.atomic.AtomicInteger;

public class TubeSessionImpl implements TubeSession, StateRepoContract {
  private static final AtomicInteger sessionIdGenerator = new AtomicInteger(0);
  private final AtomicInteger stateIdGenerator = new AtomicInteger(0);
  private final MutableIntObjectMap<StateInfo> stateRegistry = IntObjectMaps.mutable.empty();
  private final int id;
  private final int defaultPartitionCount;

  public TubeSessionImpl(int id, int defaultPartitionCount) {
    this.id = id;
    this.defaultPartitionCount = defaultPartitionCount;
  }

  public static TubeSession newInstance() {
    return new TubeSessionImpl(
        sessionIdGenerator.getAndIncrement(),
        Runtime.getRuntime().availableProcessors());
  }

  @Override
  public <TState> int register(StateRef<TState> stateRef) {
    final int stateId = stateIdGenerator.getAndIncrement();
    stateRegistry.put(stateId, new StateInfo(stateRef));
    return stateId;
  }

  @Override
  public <TState> PhaseRepo<TState> registerPhaseGroup(StateRef<TState> stateRef) {
    Preconditions.checkArgument(stateRef.getRepoId() == getId());
    final StateInfo stateInfo = stateRegistry.get(stateRef.getStateId());
    return new PhaseRepoContract<TState>() {
      private final AtomicInteger phaseIdGenerator = new AtomicInteger(0);

      @Override
      public StateRef<TState> getLenderStateRef() {
        return stateInfo.stateRef;
      }

      @Override
      public <TState1> IntIntPair register(PhaseRef<TState1> phaseRef) {
        final int stateId = TubeSessionImpl.this.register(phaseRef);
        final int phaseId = phaseIdGenerator.getAndIncrement();
        return PrimitiveTuples.pair(stateId, phaseId);
      }
    };
  }

  @SuppressWarnings("unchecked")
  @Override
  public <TState> StateRef<TState> getStateRefById(int id) {
    return (StateRef<TState>) stateRegistry.get(id).getStateRef();
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public int getDefaultPartitionCount() {
    return defaultPartitionCount;
  }

  private static class StateInfo {
    private final StateRef stateRef;
    private boolean borrowed = false;

    public StateInfo(StateRef stateRef) {
      this.stateRef = stateRef;
    }

    public StateRef getStateRef() {
      return stateRef;
    }

    public void borrow() {
      borrowed = true;
    }

    public boolean isBorrowed() {
      return borrowed;
    }
  }
}
