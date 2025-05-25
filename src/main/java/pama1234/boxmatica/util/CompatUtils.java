package pama1234.boxmatica.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

/**
 * Post Re-Write code
 */
public class CompatUtils{
  public static boolean isKeyHeld(InputUtil.Key key) {
    return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(),key.getCode());
  }
}
