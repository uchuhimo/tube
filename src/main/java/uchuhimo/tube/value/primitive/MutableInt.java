package uchuhimo.tube.value.primitive;

public class MutableInt {
  private int value;

  public MutableInt() {
    this(0);
  }

  public MutableInt(final int value) {
    this.value = value;
  }

  public int get() {
    return value;
  }

  public void set(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
