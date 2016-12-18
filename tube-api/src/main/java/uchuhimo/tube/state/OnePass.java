package uchuhimo.tube.state;

public enum OnePass implements PhaseEnum {

  Load(PhaseType.Writable),
  Update(PhaseType.Writable),
  Publish(PhaseType.ReadOnly);

  private final PhaseType phaseType;

  OnePass(PhaseType phaseType) {
    this.phaseType = phaseType;
  }

  @Override
  public PhaseType getType() {
    return phaseType;
  }
}
