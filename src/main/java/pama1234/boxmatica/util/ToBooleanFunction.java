package pama1234.boxmatica.util;

@FunctionalInterface
public interface ToBooleanFunction<R>{
  boolean applyAsBoolean(R value);
}
