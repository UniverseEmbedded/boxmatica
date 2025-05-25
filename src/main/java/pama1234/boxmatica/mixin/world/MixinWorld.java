package pama1234.boxmatica.mixin.world;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import net.minecraft.world.World;
import pama1234.boxmatica.util.IWorldUpdateSuppressor;

@Mixin(World.class)
public class MixinWorld implements IWorldUpdateSuppressor{
  @Unique
  private boolean boxmatica_preventBlockUpdates;

  @Override
  public boolean boxmatica_getShouldPreventBlockUpdates() {
    return this.boxmatica_preventBlockUpdates;
  }

  @Override
  public void boxmatica_setShouldPreventBlockUpdates(boolean preventUpdates) {
    this.boxmatica_preventBlockUpdates=preventUpdates;
  }
}
