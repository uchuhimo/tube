package uchuhimo.tube.state;

public interface StateFactory<TState> {

  TState newState(Context context);

  default void init(TState state, Context context) {
  }

  default void deinit(TState state, Context context) {
  }

  default String getInfo() {
    try {
      final TState state = newState(new Context() {
        @Override
        public int getPartitionId() {
          return 0;
        }

        @Override
        public <TState> TState getState(StateRef<TState> ref) {
          return null;
        }
      });
      return state.getClass().getSimpleName();
    } catch (Throwable throwable) {
      return "no description";
    }
  }

  interface Context {

    int getPartitionId();

    <TState> TState getState(StateRef<TState> ref);
  }
}
