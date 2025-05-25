package pama1234.boxmatica.schematic;

public record SchematicSchema(int litematicVersion,int minecraftDataVersion) {
  @Override
  public String toString() {
    return "V"+this.litematicVersion()+" / DataVersion "+this.minecraftDataVersion();
  }
}
