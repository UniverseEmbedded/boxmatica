package pama1234.boxmatica.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

import pama1234.boxmatica.schematic.BoxmaticaSchematic;
import pama1234.boxmatica.util.FileType;

public class SchematicHolder{
  private static final SchematicHolder INSTANCE=new SchematicHolder();
  private final List<BoxmaticaSchematic> schematics=new ArrayList<>();

  public static SchematicHolder getInstance() {
    return INSTANCE;
  }

  public void clearLoadedSchematics() {
    this.schematics.clear();
  }

  @Nullable
  public BoxmaticaSchematic getOrLoad(Path file) {
    for(BoxmaticaSchematic schematic:this.schematics) {
      if(file.equals(schematic.getFile())) {
        return schematic;
      }
    }

    FileType type=FileType.fromFile(file);
    BoxmaticaSchematic schematic=BoxmaticaSchematic.createFromFile(file.getParent(),file.getFileName().toString(),type);

    if(schematic!=null) {
      this.schematics.add(schematic);
    }

    return schematic;
  }

  public void addSchematic(BoxmaticaSchematic schematic,boolean allowDuplicates) {
    if(allowDuplicates||this.schematics.contains(schematic)==false) {
      if(allowDuplicates==false&&schematic.getFile()!=null) {
        for(BoxmaticaSchematic tmp:this.schematics) {
          if(schematic.getFile().equals(tmp.getFile())) {
            return;
          }
        }
      }

      this.schematics.add(schematic);
    }
  }

  public boolean removeSchematic(BoxmaticaSchematic schematic) {
    if(this.schematics.remove(schematic)) {
      DataManager.getSchematicPlacementManager().removeAllPlacementsOfSchematic(schematic);
      return true;
    }

    return false;
  }

  public Collection<BoxmaticaSchematic> getAllSchematics() {
    return this.schematics;
  }
}
