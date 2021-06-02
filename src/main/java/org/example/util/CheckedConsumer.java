package org.example.util;

import java.util.function.Consumer;

@FunctionalInterface
public interface CheckedConsumer<T, E extends Throwable> {
  static <T, E extends Throwable> Consumer<T> wrap(CheckedConsumer<T, E> checkedConsumer) {
    return t -> {
      try {
        checkedConsumer.accept(t);
      } catch (Throwable e) {
        throw new RuntimeException("Caught exception", e);
      }
    };
  }

  void accept(T t) throws E;
}
