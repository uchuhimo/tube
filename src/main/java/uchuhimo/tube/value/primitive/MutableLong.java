package uchuhimo.tube.value.primitive;

public class MutableLong {
  private long value;

  public MutableLong() {
    this(0L);
  }

  public MutableLong(long value) {
    this.value = value;
  }

  public long get() {
    return value;
  }

  public void set(long value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
