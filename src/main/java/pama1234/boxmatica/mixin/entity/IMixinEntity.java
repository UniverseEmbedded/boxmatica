package pama1234.boxmatica.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

@Mixin(Entity.class)
public interface IMixinEntity{
  @Accessor("world")
  void boxmatica_setWorld(World world);

  @Invoker("readCustomDataFromNbt")
  void boxmatica_readCustomDataFromNbt(NbtCompound nbt);
}
