package uchuhimo.tube;

import uchuhimo.tube.state.StateRepo;

import java.io.Serializable;

public interface TubeSession extends Serializable {

  int getId();

  StateRepo getStateRepo();
}
