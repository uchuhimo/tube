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
  public boolean equals(Object other) {
    return super.equals(other);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  public interface InitContext<TState> {

    StateRefImpl.InitContext<TState> getStateRefContext();

    int register(PhaseRef<TState> phaseRef);

    PhaseType getType();

    StateRef<TState> getLender();
  }
}
