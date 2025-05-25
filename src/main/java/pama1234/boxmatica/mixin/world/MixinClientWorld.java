package pama1234.boxmatica.mixin.world;

import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import pama1234.boxmatica.config.Configs;
import pama1234.boxmatica.schematic.verifier.SchematicVerifier;
import pama1234.boxmatica.util.SchematicWorldRefresher;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld extends World{
  private MixinClientWorld(MutableWorldProperties properties,
    RegistryKey<World> registryRef,
    DynamicRegistryManager manager,
    RegistryEntry<DimensionType> dimension,
    boolean isClient,boolean debugWorld,long seed,int maxChainedNeighborUpdates) {
    super(properties,registryRef,manager,dimension,isClient,debugWorld,seed,maxChainedNeighborUpdates);
  }

  @Inject(method="handleBlockUpdate",at=@At("HEAD"))
  private void boxmatica_onHandleBlockUpdate(BlockPos pos,BlockState state,int flags,CallbackInfo ci) {
    SchematicVerifier.markVerifierBlockChanges(pos);

    if(Configs.Visuals.ENABLE_RENDERING.getBooleanValue()&&
      Configs.Visuals.ENABLE_SCHEMATIC_RENDERING.getBooleanValue()) {
      SchematicWorldRefresher.INSTANCE.markSchematicChunkForRenderUpdate(pos);
    }
  }
}
