package uchuhimo.tube;

import uchuhimo.tube.state.StateRepo;

public interface TubeSession {

  int getId();

  StateRepo getStateRepo();
}
