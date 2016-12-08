package uchuhimo.tube.value.primitive;

public class MutableDouble {

  private double value;

  public MutableDouble() {
    this(0.0);
  }

  public MutableDouble(double value) {
    this.value = value;
  }

  public double get() {
    return value;
  }

  public void set(double value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
