package pama1234.boxmatica.mixin.network;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import pama1234.boxmatica.Boxmatica;
import pama1234.boxmatica.data.DataManager;

/**
 * They keep moving where the effective CustomPayload handling is... keeping them both
 */
@Mixin(ClientCommonNetworkHandler.class)
public class MixinClientCommonNetworkHandler{
  @Inject(method="onCustomPayload(Lnet/minecraft/network/packet/s2c/common/CustomPayloadS2CPacket;)V",at=@At("HEAD"))
  private void boxmatica_onCustomPayload(CustomPayloadS2CPacket packet,CallbackInfo ci) {
    if(packet.payload().getId().id().equals(DataManager.CARPET_HELLO)) {
      Boxmatica.debugLog("ClientCommonNetworkHandler#boxmatica_onCustomPayload(): received carpet hello packet");
      DataManager.setIsCarpetServer(true);
    }
  }
}
