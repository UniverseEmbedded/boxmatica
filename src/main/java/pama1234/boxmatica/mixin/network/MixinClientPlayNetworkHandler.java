package pama1234.boxmatica.mixin.network;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.NbtQueryResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import pama1234.boxmatica.Boxmatica;
import pama1234.boxmatica.config.Configs;
import pama1234.boxmatica.data.DataManager;
import pama1234.boxmatica.data.EntitiesDataStorage;
import pama1234.boxmatica.util.SchematicWorldRefresher;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler{
  @Inject(method="onChunkData",at=@At("RETURN"))
  private void boxmatica_onUpdateChunk(ChunkDataS2CPacket packet,CallbackInfo ci) {
    int chunkX=packet.getChunkX();
    int chunkZ=packet.getChunkZ();
    //Boxmatica.debugLog("MixinClientPlayNetworkHandler#boxmatica_onUpdateChunk({}, {})", chunkX, chunkZ);

    if(Configs.Visuals.ENABLE_RENDERING.getBooleanValue()&&
      Configs.Visuals.ENABLE_SCHEMATIC_RENDERING.getBooleanValue()) {
      SchematicWorldRefresher.INSTANCE.markSchematicChunksForRenderUpdate(chunkX,chunkZ);
    }

    DataManager.getSchematicPlacementManager().onClientChunkLoad(chunkX,chunkZ);
    // TODO verifier updates?
  }

  @Inject(method="onUnloadChunk",at=@At("RETURN"))
  private void boxmatica_onChunkUnload(UnloadChunkS2CPacket packet,CallbackInfo ci) {
    if(Configs.Generic.LOAD_ENTIRE_SCHEMATICS.getBooleanValue()==false) {
      //Boxmatica.debugLog("MixinClientPlayNetworkHandler#boxmatica_onChunkUnload({}, {})", packet.pos().x, packet.pos().z);
      DataManager.getSchematicPlacementManager().onClientChunkUnload(packet.pos().x,packet.pos().z);
    }
  }

  @Inject(method="onGameMessage",cancellable=true,at=@At(value="INVOKE",target="Lnet/minecraft/client/network/message/MessageHandler;onGameMessage(Lnet/minecraft/text/Text;Z)V"))
  private void boxmatica_onGameMessage(GameMessageS2CPacket packet,CallbackInfo ci) {
    if(DataManager.onChatMessage(packet.content())) {
      ci.cancel();
    }
  }

  /**
   * They keep moving where the effective onCustomPayload handling is... keeping them both
   */
  @Inject(method="onCustomPayload",at=@At("HEAD"))
  private void boxmatica_onCustomPayload(CustomPayload payload,CallbackInfo ci) {
    if(payload.getId().id().equals(DataManager.CARPET_HELLO)) {
      Boxmatica.debugLog("MixinClientPlayNetworkHandler#boxmatica_onCustomPayload(): received carpet hello packet");
      DataManager.setIsCarpetServer(true);
    }
  }

  @Inject(method="onNbtQueryResponse",at=@At(value="INVOKE",target="Lnet/minecraft/client/network/DataQueryHandler;handleQueryResponse(ILnet/minecraft/nbt/NbtCompound;)Z"))
  private void boxmatica_onQueryResponse(NbtQueryResponseS2CPacket packet,CallbackInfo ci) {
    if(Configs.Generic.ENTITY_DATA_SYNC_BACKUP.getBooleanValue()) {
      EntitiesDataStorage.getInstance().handleVanillaQueryNbt(packet.getTransactionId(),packet.getNbt());
    }
  }

  @Inject(method="onCommandTree",at=@At("RETURN"))
  private void minihud_onCommandTree(CallbackInfo ci) {
    if(Configs.Generic.ENTITY_DATA_SYNC_BACKUP.getBooleanValue()) {
      // when the player becomes OP, the server sends the command tree to the client
      EntitiesDataStorage.getInstance().resetOpCheck();
    }
  }
}
