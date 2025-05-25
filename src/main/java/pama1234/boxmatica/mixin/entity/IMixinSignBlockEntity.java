package pama1234.boxmatica.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;

@Mixin(SignBlockEntity.class)
public interface IMixinSignBlockEntity{
  @Accessor("frontText")
  SignText boxmatica_getFrontText();

  @Accessor("backText")
  SignText boxmatica_getBackText();
}
