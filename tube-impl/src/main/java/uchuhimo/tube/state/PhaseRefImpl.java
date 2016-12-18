package uchuhimo.tube.state;

public class PhaseRefImpl<TState> extends StateRefImpl<TState> implements PhaseRef<TState> {

  private final int id;
  private final PhaseType type;
  private final StateRef<TState> lender;

  public PhaseRefImpl(InitContext<TState> context) {
    super(context.getStateRefContext());
    this.id = context.register(this);
    this.type = context.getType();
    this.lender = context.getLender();
  }

  @Override
  public int getPhaseId() {
    return id;
  }

  @Override
  public PhaseType getPhaseType() {
    return type;
  }

  @Override
  public StateRef<TState> getLender() {
    return lender;
  }

  @Override
  public String toString() {
    return "PhaseRefRef[" + getStateId() + "]{"
        + "phase id=" + getPhaseId()
        + ", type=" + getPhaseType().name()
        + ", factory=" + getFactory().getInfo()
        + ", partitionCount=" + getPartitionCount()
        + ", lender=" + getLender().getStateId()
        + ", borrowed=" + isBorrowed()
        + ", repo=" + getRepoId()
        + '}';
  }

  public interface InitContext<TState> {

    StateRefImpl.InitContext<TState> getStateRefContext();

    int register(PhaseRef<TState> phaseRef);

    PhaseType getType();

    StateRef<TState> getLender();
  }
}
