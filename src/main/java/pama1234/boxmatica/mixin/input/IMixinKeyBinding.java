package pama1234.boxmatica.mixin.input;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Post Re-Write code
 */
@Mixin(KeyBinding.class)
public interface IMixinKeyBinding{
  @Accessor("boundKey")
  InputUtil.Key boxmatica_getBoundKey();
}
