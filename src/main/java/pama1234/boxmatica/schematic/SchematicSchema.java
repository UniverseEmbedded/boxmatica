package pama1234.boxmatica.schematic;

public record SchematicSchema(int boxmaticVersion,int minecraftDataVersion) {
  @Override
  public String toString() {
    return "V"+this.boxmaticVersion()+" / DataVersion "+this.minecraftDataVersion();
  }
}
