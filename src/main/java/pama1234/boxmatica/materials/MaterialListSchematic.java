package pama1234.boxmatica.materials;

import java.util.Collection;
import com.google.common.collect.ImmutableList;
import pama1234.boxmatica.schematic.BoxmaticaSchematic;
import fi.dy.masa.malilib.util.StringUtils;

public class MaterialListSchematic extends MaterialListBase{
  private final BoxmaticaSchematic schematic;
  private final ImmutableList<String> regions;

  public MaterialListSchematic(BoxmaticaSchematic schematic,boolean reCreate) {
    this(schematic,schematic.getAreas().keySet(),reCreate);
  }

  public MaterialListSchematic(BoxmaticaSchematic schematic,Collection<String> subRegions,boolean reCreate) {
    super();

    this.schematic=schematic;
    this.regions=ImmutableList.copyOf(subRegions);

    if(reCreate) {
      this.reCreateMaterialList();
    }
  }

  @Override
  public void reCreateMaterialList() {
    this.materialListAll=ImmutableList.copyOf(MaterialListUtils.createMaterialListFor(this.schematic,this.regions));
    this.refreshPreFilteredList();
    this.updateCounts();
  }

  @Override
  public String getName() {
    return this.schematic.getMetadata().getName();
  }

  @Override
  public String getTitle() {
    return StringUtils.translate("boxmatica.gui.title.material_list.schematic",this.getName(),this.regions.size(),this.schematic.getAreas().size());
  }
}
