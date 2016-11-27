package uchuhimo.tube.state;

public interface StateRefContainer {
  <TState> StateRef<TState> getStateRefById(int id);

  default <TState> StateRef<TState> getStateRefById(int id, Class<TState> clazz) {
    return getStateRefById(id);
  }
}
