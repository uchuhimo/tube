package uchuhimo.tube;

public class TubeRuntimeException extends RuntimeException {

  public TubeRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public TubeRuntimeException(String message) {
    super(message);
  }
}
