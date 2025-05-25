package pama1234.boxmatica.materials;

import pama1234.boxmatica.scheduler.TaskScheduler;
import pama1234.boxmatica.scheduler.tasks.TaskCountBlocksArea;
import pama1234.boxmatica.selection.AreaSelection;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class MaterialListAreaAnalyzer extends MaterialListBase{
  private final AreaSelection selection;

  public MaterialListAreaAnalyzer(AreaSelection selection) {
    super();

    this.selection=selection;
  }

  @Override
  public String getName() {
    return this.selection.getName();
  }

  @Override
  public String getTitle() {
    return StringUtils.translate("boxmatica.gui.title.material_list.area_analyzer",this.getName());
  }

  @Override
  public void reCreateMaterialList() {
    TaskCountBlocksArea task=new TaskCountBlocksArea(this.selection,this);
    TaskScheduler.getInstanceClient().scheduleTask(task,20);
    InfoUtils.showGuiOrInGameMessage(MessageType.INFO,"boxmatica.message.scheduled_task_added");
  }
}
