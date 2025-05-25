package pama1234.boxmatica.mixin.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractBlock.class)
public interface IMixinAbstractBlock{
  @Invoker("getPickStack")
  ItemStack boxmatica_getPickStack(WorldView worldView,BlockPos blockPos,BlockState blockState,boolean bl);
}
