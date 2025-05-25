package pama1234.boxmatica.event;

import javax.annotation.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.datafixer.fix.BlockStateFlattening;
import net.minecraft.registry.DynamicRegistryManager;

import fi.dy.masa.malilib.interfaces.IWorldLoadListener;
import pama1234.boxmatica.Boxmatica;
import pama1234.boxmatica.compat.jade.JadeCompat;
import pama1234.boxmatica.data.DataManager;
import pama1234.boxmatica.data.EntitiesDataStorage;
import pama1234.boxmatica.schematic.conversion.SchematicConversionMaps;
import pama1234.boxmatica.world.SchematicWorldHandler;

public class WorldLoadListener implements IWorldLoadListener{
  @Override
  public void onWorldLoadImmutable(DynamicRegistryManager.Immutable immutable) {
    // Save the DynamicRegistry before the IntegratedServer even launches, when possible
    SchematicWorldHandler.INSTANCE.setDynamicRegistryManager(immutable);
  }

  @Override
  public void onWorldLoadPre(@Nullable ClientWorld worldBefore,@Nullable ClientWorld worldAfter,MinecraftClient mc) {
    // Save the settings before the integrated server gets shut down
    if(worldBefore!=null) {
      DataManager.save();
    }
    if(worldAfter!=null) {
      JadeCompat.checkForJade();
      EntitiesDataStorage.getInstance().onWorldPre();
      DataManager.getInstance().onWorldPre(worldAfter.getRegistryManager());
    }
  }

  @Override
  public void onWorldLoadPost(@Nullable ClientWorld worldBefore,@Nullable ClientWorld worldAfter,MinecraftClient mc) {
    SchematicWorldHandler.INSTANCE.recreateSchematicWorld(worldAfter==null);
    DataManager.getInstance().reset(worldAfter==null);
    EntitiesDataStorage.getInstance().reset(worldAfter==null);

    if(worldAfter!=null) {
      DataManager.load();
      Boxmatica.debugLog("onWorldLoadPost(): Init BlockStateFlattening DataFixer [Test: {}]",BlockStateFlattening.lookupBlock("minecraft:air"));
      SchematicConversionMaps.computeMaps();
      EntitiesDataStorage.getInstance().onWorldJoin();
    }else {
      DataManager.clear();
    }
  }
}
