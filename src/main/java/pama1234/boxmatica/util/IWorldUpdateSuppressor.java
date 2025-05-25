package pama1234.boxmatica.util;

public interface IWorldUpdateSuppressor{
  boolean boxmatica_getShouldPreventBlockUpdates();

  void boxmatica_setShouldPreventBlockUpdates(boolean preventUpdates);
}
