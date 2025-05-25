package pama1234.boxmatica.scheduler.tasks;

import java.util.Collection;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import pama1234.boxmatica.data.DataManager;
import pama1234.boxmatica.materials.IMaterialList;
import pama1234.boxmatica.schematic.placement.SchematicPlacement;
import pama1234.boxmatica.schematic.placement.SubRegionPlacement.RequiredEnabled;
import pama1234.boxmatica.selection.Box;
import pama1234.boxmatica.util.BlockInfoListType;

public class TaskCountBlocksPlacement extends TaskCountBlocksBase{
  protected final SchematicPlacement schematicPlacement;
  protected final boolean ignoreState;

  public TaskCountBlocksPlacement(SchematicPlacement schematicPlacement,IMaterialList materialList) {
    this(schematicPlacement,materialList,false);
  }

  public TaskCountBlocksPlacement(SchematicPlacement schematicPlacement,IMaterialList materialList,boolean ignoreState) {
    super(materialList,"boxmatica.gui.label.task_name.material_list");

    this.schematicPlacement=schematicPlacement;
    this.ignoreState=ignoreState;
    Collection<Box> boxes=schematicPlacement.getSubRegionBoxes(RequiredEnabled.PLACEMENT_ENABLED).values();

    // Filter/clamp the boxes to intersect with the render layer
    if(materialList.getMaterialListType()==BlockInfoListType.RENDER_LAYERS) {
      this.addPerChunkBoxes(boxes,DataManager.getRenderLayerRange());
    }else {
      this.addPerChunkBoxes(boxes);
    }

  }

  @Override
  public boolean canExecute() {
    return super.canExecute()&&this.schematicWorld!=null;
  }

  @Override
  protected void countAtPosition(BlockPos pos) {
    BlockState stateSchematic=this.schematicWorld.getBlockState(pos);

    if(stateSchematic.isAir()==false) {
      BlockState stateClient=this.clientWorld.getBlockState(pos);

      this.countsTotal.addTo(stateSchematic,1);

      if(stateClient.isAir()) {
        this.countsMissing.addTo(stateSchematic,1);
      }else if(stateClient!=stateSchematic&&
        (this.ignoreState==false||stateClient.getBlock()!=stateSchematic.getBlock())) {
          this.countsMissing.addTo(stateSchematic,1);
          this.countsMismatch.addTo(stateSchematic,1);
        }
    }
  }
}
