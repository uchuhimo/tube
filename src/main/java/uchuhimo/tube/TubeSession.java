package uchuhimo.tube;

import uchuhimo.tube.state.StateRepo;

import java.io.Serializable;

public interface TubeSession extends StateRepo, Serializable {

  static TubeSession newInstance() {
    return TubeSessionImpl.newInstance();
  }

  int getId();
}
