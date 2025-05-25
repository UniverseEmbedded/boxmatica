package pama1234.boxmatica.event;

import java.util.function.Supplier;
import org.joml.Matrix4f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.Frustum;
import net.minecraft.util.profiler.Profiler;

import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.util.GuiUtils;
import pama1234.boxmatica.Reference;
import pama1234.boxmatica.config.Configs;
import pama1234.boxmatica.data.DataManager;
import pama1234.boxmatica.gui.GuiSchematicManager;
import pama1234.boxmatica.render.BoxmaticaRenderer;
import pama1234.boxmatica.render.OverlayRenderer;
import pama1234.boxmatica.render.infohud.InfoHud;
import pama1234.boxmatica.render.infohud.ToolHud;
import pama1234.boxmatica.tool.ToolMode;

public class RenderHandler implements IRenderer{
  @Override
  public void onRenderWorldPreWeather(Framebuffer fb,Matrix4f posMatrix,Matrix4f projMatrix,Frustum frustum,Camera camera,Fog fog,BufferBuilderStorage buffers,Profiler profiler) {
    //        MinecraftClient mc = MinecraftClient.getInstance();
    //
    //        if (Configs.Visuals.ENABLE_RENDERING.getBooleanValue() && mc.player != null)
    //        {
    //        }
  }

  @Override
  public void onRenderWorldLastAdvanced(Framebuffer fb,Matrix4f posMatrix,Matrix4f projMatrix,Frustum frustum,Camera camera,Fog fog,BufferBuilderStorage buffers,Profiler profiler) {
    MinecraftClient mc=MinecraftClient.getInstance();

    if(Configs.Visuals.ENABLE_RENDERING.getBooleanValue()&&mc.player!=null) {
      profiler.push("overlay_boxes");
      OverlayRenderer.getInstance().renderBoxes(posMatrix,profiler);

      if(Configs.InfoOverlays.VERIFIER_OVERLAY_ENABLED.getBooleanValue()) {
        profiler.swap("overlay_mismatches");
        OverlayRenderer.getInstance().renderSchematicVerifierMismatches(posMatrix,profiler);
      }

      if(DataManager.getToolMode()==ToolMode.REBUILD) {
        profiler.swap("overlay_targeting");
        OverlayRenderer.getInstance().renderSchematicRebuildTargetingOverlay(posMatrix,profiler);
      }

      // Schematic Overlay Rendering
      profiler.swap("schematic_overlay");
      BoxmaticaRenderer.getInstance().piecewiseRenderOverlay(null,null,profiler);
      profiler.pop();
    }
  }

  @Override
  public Supplier<String> getProfilerSectionSupplier() {
    return ()->Reference.MOD_ID+"_render_handler";
  }

  @Override
  public void onRenderGameOverlayPostAdvanced(DrawContext drawContext,float partialTicks,Profiler profiler,MinecraftClient mc) {
    if(Configs.Visuals.ENABLE_RENDERING.getBooleanValue()&&mc.player!=null) {
      profiler.push("overlay_hud");
      // The Info HUD renderers can decide if they want to be rendered in GUIs
      InfoHud.getInstance().renderHud(drawContext);

      if(GuiUtils.getCurrentScreen()==null) {
        if(mc.options.hudHidden==false) {
          ToolHud.getInstance().renderHud(drawContext);
          profiler.swap("overlay_hover_info");
          OverlayRenderer.getInstance().renderHoverInfo(mc,drawContext,profiler);
        }

        if(GuiSchematicManager.hasPendingPreviewTask()) {
          profiler.swap("overlay_preview_frame");
          OverlayRenderer.getInstance().renderPreviewFrame(mc,drawContext,profiler);
        }
      }

      profiler.pop();
    }
  }
}
