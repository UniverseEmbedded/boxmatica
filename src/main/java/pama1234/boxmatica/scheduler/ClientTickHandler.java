package pama1234.boxmatica.scheduler;

import net.minecraft.client.MinecraftClient;

import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import fi.dy.masa.malilib.util.EntityUtils;
import pama1234.boxmatica.config.Configs;
import pama1234.boxmatica.data.DataManager;
import pama1234.boxmatica.selection.SelectionManager;
import pama1234.boxmatica.util.WorldUtils;

public class ClientTickHandler implements IClientTickHandler{
  @Override
  public void onClientTick(MinecraftClient mc) {
    if(mc.world!=null&&mc.player!=null) {
      SelectionManager sm=DataManager.getSelectionManager();

      if(sm.hasGrabbedElement()) {
        sm.moveGrabbedElement(mc.player);
      }

      if(mc.currentScreen==null) {
        /*
         * if (Configs.Generic.EASY_PLACE_POST_REWRITE.getBooleanValue())
         * {
         * EasyPlaceUtils.easyPlaceOnUseTick();
         * }
         * else
         * {
         */
        WorldUtils.easyPlaceOnUseTick(mc);
        //}
      }

      if(Configs.Generic.LAYER_MODE_DYNAMIC.getBooleanValue()) {
        DataManager.getRenderLayerRange().setSingleBoundaryToPosition(EntityUtils.getCameraEntity());
      }

      DataManager.getSchematicPlacementManager().processQueuedChunks();
      TaskScheduler.getInstanceClient().runTasks();
    }
  }
}
