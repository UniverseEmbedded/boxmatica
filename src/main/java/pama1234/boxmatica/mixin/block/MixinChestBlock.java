package pama1234.boxmatica.mixin.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.util.BlockMirror;

import pama1234.boxmatica.config.Configs;
import pama1234.boxmatica.util.BlockUtils;

@Mixin(ChestBlock.class)
public class MixinChestBlock{
  @Inject(method="mirror",at=@At("HEAD"),cancellable=true)
  private void boxmatica_fixChestMirror(BlockState state,BlockMirror mirror,CallbackInfoReturnable<BlockState> cir) {
    ChestType type=state.get(ChestBlock.CHEST_TYPE);

    if(Configs.Generic.FIX_CHEST_MIRROR.getBooleanValue()&&type!=ChestType.SINGLE) {
      state=BlockUtils.fixMirrorDoubleChest(state,mirror,type);
      cir.setReturnValue(state);
    }
  }
}
