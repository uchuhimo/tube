package uchuhimo.tube.value.primitive;

public class MutableString {

  private String value;

  public MutableString() {
    this("");
  }

  public MutableString(String value) {
    this.value = value;
  }

  public String get() {
    return value;
  }

  public void set(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
