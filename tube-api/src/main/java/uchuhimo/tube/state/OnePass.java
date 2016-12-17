package uchuhimo.tube.state;

public enum OnePass implements PhaseEnum {

  Load {
    @Override
    public PhaseType getType() {
      return PhaseType.Writable;
    }
  },
  Update {
    @Override
    public PhaseType getType() {
      return PhaseType.Writable;
    }
  },
  Publish {
    @Override
    public PhaseType getType() {
      return PhaseType.ReadOnly;
    }
  }
}
