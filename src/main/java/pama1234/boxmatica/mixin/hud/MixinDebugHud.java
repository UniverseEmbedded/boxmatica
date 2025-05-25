package pama1234.boxmatica.mixin.hud;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.gui.hud.DebugHud;

import pama1234.boxmatica.data.DataManager;
import pama1234.boxmatica.render.BoxmaticaRenderer;
import pama1234.boxmatica.render.schematic.WorldRendererSchematic;
import pama1234.boxmatica.util.EntityUtils;
import pama1234.boxmatica.world.SchematicWorldHandler;
import pama1234.boxmatica.world.WorldSchematic;
import fi.dy.masa.malilib.gui.GuiBase;

@Mixin(DebugHud.class)
public abstract class MixinDebugHud{
  @Inject(method="getLeftText",at=@At("RETURN"))
  private void boxmatica_addDebugLines(CallbackInfoReturnable<List<String>> cir) {
    WorldSchematic world=SchematicWorldHandler.getSchematicWorld();

    if(world!=null) {
      List<String> list=cir.getReturnValue();
      Pair<String,String> pair=EntityUtils.getEntityDebug();
      String pre=GuiBase.TXT_GOLD;
      String rst=GuiBase.TXT_RST;

      WorldRendererSchematic renderer=BoxmaticaRenderer.getInstance().getWorldRenderer();

      list.add(String.format("%s[Boxmatica]%s %s",
        pre,rst,renderer.getDebugInfoRenders()));

      String str=String.format("E: %d TE: %d C: %d, CT: %d, CV: %d",
        world.getRegularEntityCount(),
        //                                       world.getEntityDebug(),
        world.getChunkProvider().getTileEntityCount(),
        world.getChunkProvider().getLoadedChunkCount(),
        DataManager.getSchematicPlacementManager().getTouchedChunksCount(),
        DataManager.getSchematicPlacementManager().getLastVisibleChunksCount());

      list.add(String.format("%s[Boxmatica]%s %s %s",pre,rst,renderer.getDebugInfoEntities(),str));

      if(!pair.getLeft().isEmpty()) {
        list.add(String.format("%s[%s]%s %s",pre,pair.getLeft(),rst,pair.getRight()));
      }
    }
  }
}
