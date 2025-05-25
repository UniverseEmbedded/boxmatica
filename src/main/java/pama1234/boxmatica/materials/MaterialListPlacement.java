package pama1234.boxmatica.materials;

import pama1234.boxmatica.config.Configs;
import pama1234.boxmatica.scheduler.TaskScheduler;
import pama1234.boxmatica.scheduler.tasks.TaskCountBlocksPlacement;
import pama1234.boxmatica.schematic.placement.SchematicPlacement;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class MaterialListPlacement extends MaterialListBase{
  private final SchematicPlacement placement;

  public MaterialListPlacement(SchematicPlacement placement) {
    this(placement,false);
  }

  public MaterialListPlacement(SchematicPlacement placement,boolean reCreate) {
    super();

    this.placement=placement;

    if(reCreate) {
      this.reCreateMaterialList();
    }
  }

  @Override
  public boolean supportsRenderLayers() {
    return true;
  }

  @Override
  public String getName() {
    return this.placement.getName();
  }

  @Override
  public String getTitle() {
    return StringUtils.translate("boxmatica.gui.title.material_list.placement",this.getName());
  }

  @Override
  public void reCreateMaterialList() {
    boolean ignoreState=Configs.Generic.MATERIAL_LIST_IGNORE_STATE.getBooleanValue();
    TaskCountBlocksPlacement task=new TaskCountBlocksPlacement(this.placement,this,ignoreState);
    TaskScheduler.getInstanceClient().scheduleTask(task,20);
    InfoUtils.showGuiOrInGameMessage(MessageType.INFO,"boxmatica.message.scheduled_task_added");
  }
}
