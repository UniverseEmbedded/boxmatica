package pama1234.boxmatica.scheduler.tasks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import pama1234.boxmatica.materials.IMaterialList;
import pama1234.boxmatica.selection.AreaSelection;

public class TaskCountBlocksArea extends TaskCountBlocksBase{
  public TaskCountBlocksArea(AreaSelection selection,IMaterialList materialList) {
    super(materialList,"boxmatica.gui.label.task_name.area_analyzer");

    this.addPerChunkBoxes(selection.getAllSubRegionBoxes());
  }

  @Override
  protected void countAtPosition(BlockPos pos) {
    BlockState stateClient=this.clientWorld.getBlockState(pos);
    this.countsTotal.addTo(stateClient,1);
  }
}
