package pama1234.boxmatica.compat.lwgl;

import fi.dy.masa.malilib.compat.lwgl.GpuCompat;
import pama1234.boxmatica.config.Configs;

/**
 * Makes an attempt to adjust Visual Configs for different GPU Models to help reduce crashes
 */
public class RenderCompat{
  public static void checkGpuVisuals() {
    if(Configs.Visuals.SCHEMATIC_OVERLAY_ENABLE_RESORTING.getBooleanValue()&&!GpuCompat.isNvidiaGpu()) {
      Configs.Visuals.SCHEMATIC_OVERLAY_ENABLE_RESORTING.setBooleanValue(false);
    }
  }
}
