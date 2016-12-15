package uchuhimo.tube;

import uchuhimo.tube.state.StateFactory;
import uchuhimo.tube.state.StateRef;
import uchuhimo.tube.state.StateRepo;
import uchuhimo.tube.state.StateRepoImpl;
import uchuhimo.tube.value.primitive.MutableDouble;
import uchuhimo.tube.value.primitive.MutableInt;
import uchuhimo.tube.value.primitive.MutableLong;
import uchuhimo.tube.value.tuple.Tuple2;
import uchuhimo.tube.value.tuple.Tuple3;

import java.util.Collection;
import java.util.Map;

public class TubeSessionImpl implements TubeSession {

  private static int nextSessionId = 0;
  private final int id;
  private final StateRepo stateRepo;

  public TubeSessionImpl(InitContext context) {
    this.id = context.register(this);
    this.stateRepo = context.getStateRepo();
  }

  public static TubeSession newInstance() {
    return new TubeSessionImpl(new InitContext() {
      private int sessionId;

      @Override
      public int register(TubeSession session) {
        sessionId = TubeSessionImpl.nextSessionId;
        nextSessionId++;
        return sessionId;
      }

      @Override
      public StateRepo getStateRepo() {
        return new StateRepoImpl(new StateRepoImpl.InitContext() {
          @Override
          public int register(StateRepo stateRepo) {
            return sessionId;
          }

          @Override
          public int getDefaultPartitionCount() {
            return Runtime.getRuntime().availableProcessors();
          }
        });
      }
    });
  }

  @Override
  public Collection<StateRef<?>> getRootRefs() {
    return stateRepo.getRootRefs();
  }

  @Override
  public <TState> StateRef<TState> getStateRefById(int id) {
    return stateRepo.getStateRefById(id);
  }

  @Override
  public int getDefaultPartitionCount() {
    return stateRepo.getDefaultPartitionCount();
  }

  @Override
  public StateRef<MutableInt> newInt(int partitionCount) {
    return stateRepo.newInt(partitionCount);
  }

  @Override
  public StateRef<MutableLong> newLong(int partitionCount) {
    return stateRepo.newLong(partitionCount);
  }

  @Override
  public StateRef<MutableDouble> newDouble(int partitionCount) {
    return stateRepo.newDouble(partitionCount);
  }

  @Override
  public <TKey, TValue> StateRef<Map<TKey, TValue>> newMap(int partitionCount) {
    return stateRepo.newMap(partitionCount);
  }

  @Override
  public <T1, T2> StateRef<Tuple2<T1, T2>> newTuple2(
      StateRef<T1> element1, StateRef<T2> element2, int partitionCount) {
    return stateRepo.newTuple2(element1, element2, partitionCount);
  }

  @Override
  public <T1, T2, T3> StateRef<Tuple3<T1, T2, T3>> newTuple3(
      StateRef<T1> element1, StateRef<T2> element2, StateRef<T3> element3, int partitionCount) {
    return stateRepo.newTuple3(element1, element2, element3, partitionCount);
  }

  @Override
  public <TState> StateRef<TState> newBy(StateFactory<TState> factory, int partitionCount) {
    return stateRepo.newBy(factory, partitionCount);
  }

  @Override
  public int getId() {
    return id;
  }

  public interface InitContext {

    int register(TubeSession session);

    StateRepo getStateRepo();
  }
}
