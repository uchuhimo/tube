package uchuhimo.tube;

import uchuhimo.tube.state.StateRepo;

import java.io.Serializable;

public interface TubeSession extends Serializable {

  static TubeSession newInstance() {
    return TubeSessionImpl.newInstance();
  }

  int getId();

  StateRepo getStateRepo();
}
