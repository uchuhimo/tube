package uchuhimo.tube.util;

import uchuhimo.tube.TubeRuntimeException;

public enum Thrown {
  ;

  public static UnreachableCodeException unreachable() {
    return new UnreachableCodeException();
  }

  public static class UnreachableCodeException extends TubeRuntimeException {
    public UnreachableCodeException() {
      super("unreachable code");
    }
  }
}
