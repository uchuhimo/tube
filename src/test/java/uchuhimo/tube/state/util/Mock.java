package uchuhimo.tube.state.util;

import uchuhimo.tube.state.StateFactory;
import uchuhimo.tube.state.StateRef;
import uchuhimo.tube.state.StateRepo;
import uchuhimo.tube.state.StateRepoImpl;
import uchuhimo.tube.util.Utility;

@Utility
public final class Mock {

  private Mock() {
  }

  public static StateFactory.Context newStateFactoryContext() {
    return new MockStateFactoryContext();
  }

  public static StateRepo newStateRepo() {
    return new MockStateRepo();
  }

  private static class MockStateFactoryContext implements StateFactory.Context {
    @Override
    public int getPartitionId() {
      return 1;
    }

    @Override
    public <TState> TState getState(StateRef<TState> ref) {
      return ref.getFactory().newState(new MockStateFactoryContext());
    }
  }

  private static class MockStateRepo extends StateRepoImpl {
    public MockStateRepo() {
      super(new InitContext() {
        private int nextId = -1;

        @Override
        public int register(StateRepo stateRepo) {
          nextId++;
          return nextId;
        }

        @Override
        public int getDefaultPartitionCount() {
          return 1;
        }
      });
    }
  }
}
