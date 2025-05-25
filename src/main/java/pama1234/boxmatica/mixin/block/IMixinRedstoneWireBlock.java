package pama1234.boxmatica.mixin.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@Mixin(RedstoneWireBlock.class)
public interface IMixinRedstoneWireBlock{
  @Invoker("getPlacementState")
  BlockState boxmatica_GetPlacementState(BlockView world,BlockState state,BlockPos pos);
}
