package uchuhimo.tube.function;

import java.io.Serializable;
import java.util.function.Supplier;

@FunctionalInterface
public interface Function0<TResult> extends Supplier<TResult>, Serializable {
  TResult apply();

  @Override
  default TResult get() {
    return apply();
  }
}
