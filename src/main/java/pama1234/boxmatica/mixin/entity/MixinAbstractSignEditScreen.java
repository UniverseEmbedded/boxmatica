package pama1234.boxmatica.mixin.entity;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;

import pama1234.boxmatica.config.Configs;
import pama1234.boxmatica.util.WorldUtils;

@Mixin(value=AbstractSignEditScreen.class,priority=990)
public class MixinAbstractSignEditScreen{
  @Shadow
  @Final
  protected SignBlockEntity blockEntity;
  @Shadow
  @Final
  private String[] messages;
  @Shadow
  @Final
  private boolean front;

  @Inject(method="init",at=@At("HEAD"))
  private void boxmatica_insertSignText(CallbackInfo ci) {
    if(Configs.Generic.SIGN_TEXT_PASTE.getBooleanValue()) {
      WorldUtils.insertSignTextFromSchematic(this.blockEntity,this.messages,this.front);
    }
  }
}
