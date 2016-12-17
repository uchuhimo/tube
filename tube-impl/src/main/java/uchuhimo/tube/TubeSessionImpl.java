package uchuhimo.tube;

import uchuhimo.tube.state.StateRepo;
import uchuhimo.tube.state.StateRepoImpl;

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
  public int getId() {
    return id;
  }

  @Override
  public StateRepo getStateRepo() {
    return stateRepo;
  }

  public interface InitContext {

    int register(TubeSession session);

    StateRepo getStateRepo();
  }
}
