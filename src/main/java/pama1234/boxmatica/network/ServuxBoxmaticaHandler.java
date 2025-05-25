package pama1234.boxmatica.network;

import javax.annotation.Nullable;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import fi.dy.masa.malilib.network.IClientPayloadData;
import fi.dy.masa.malilib.network.IPluginClientPlayHandler;
import fi.dy.masa.malilib.network.PacketSplitter;
import pama1234.boxmatica.Boxmatica;
import pama1234.boxmatica.data.DataManager;
import pama1234.boxmatica.data.EntitiesDataStorage;
import pama1234.boxmatica.schematic.BoxmaticaSchematic;
import pama1234.boxmatica.schematic.placement.SchematicPlacement;

@Environment(EnvType.CLIENT)
public abstract class ServuxBoxmaticaHandler<T extends CustomPayload> implements IPluginClientPlayHandler<T>{
  private final static ServuxBoxmaticaHandler<ServuxBoxmaticaPacket.Payload> INSTANCE=new ServuxBoxmaticaHandler<>() {
    @Override
    public void receive(ServuxBoxmaticaPacket.Payload payload,ClientPlayNetworking.Context context) {
      ServuxBoxmaticaHandler.INSTANCE.receivePlayPayload(payload,context);
    }
  };
  public static ServuxBoxmaticaHandler<ServuxBoxmaticaPacket.Payload> getInstance() {
    return INSTANCE;
  }

  public static final Identifier CHANNEL_ID=Identifier.of("servux","boxmatics");

  private boolean servuxRegistered;
  private boolean payloadRegistered=false;
  private int failures=0;
  private static final int MAX_FAILURES=4;
  private long readingSessionKey=-1;

  @Override
  public Identifier getPayloadChannel() {
    return CHANNEL_ID;
  }

  @Override
  public boolean isPlayRegistered(Identifier channel) {
    if(channel.equals(CHANNEL_ID)) {
      return this.payloadRegistered;
    }

    return false;
  }

  @Override
  public void setPlayRegistered(Identifier channel) {
    if(channel.equals(CHANNEL_ID)) {
      this.payloadRegistered=true;
    }
  }

  @Override
  public <P extends IClientPayloadData> void decodeClientData(Identifier channel,P data) {
    ServuxBoxmaticaPacket packet=(ServuxBoxmaticaPacket)data;

    if(!channel.equals(CHANNEL_ID)) {
      return;
    }
    switch(packet.getType()) {
      case PACKET_S2C_METADATA-> {
        if(EntitiesDataStorage.getInstance().receiveServuxMetadata(packet.getCompound())) {
          this.servuxRegistered=true;
        }
      }
      case PACKET_S2C_BLOCK_NBT_RESPONSE_SIMPLE->EntitiesDataStorage.getInstance().handleBlockEntityData(packet.getPos(),packet.getCompound(),null);
      case PACKET_S2C_ENTITY_NBT_RESPONSE_SIMPLE->EntitiesDataStorage.getInstance().handleEntityData(packet.getEntityId(),packet.getCompound());
      case PACKET_S2C_NBT_RESPONSE_DATA-> {
        if(this.readingSessionKey==-1) {
          this.readingSessionKey=Random.create(Util.getMeasuringTimeMs()).nextLong();
        }

        //Boxmatica.debugLog("ServuxBoxmaticaHandler#decodeClientData(): received Entity Data Packet Slice of size {} (in bytes) // reading session key [{}]", packet.getTotalSize(), this.readingSessionKey);
        PacketByteBuf fullPacket=PacketSplitter.receive(this,this.readingSessionKey,packet.getBuffer());

        if(fullPacket!=null) {
          try {
            this.readingSessionKey=-1;
            this.handleBulkData(fullPacket.readVarInt(),(NbtCompound)fullPacket.readNbt(NbtSizeTracker.ofUnlimitedBytes()));
          }catch(Exception e) {
            Boxmatica.LOGGER.error("ServuxBoxmaticaHandler#decodeClientData(): Entity Data: error reading fullBuffer [{}]",e.getLocalizedMessage());
          }
        }
      }
      default->Boxmatica.LOGGER.warn("ServuxBoxmaticaHandler#decodeClientData(): received unhandled packetType {} of size {} bytes.",packet.getPacketType(),packet.getTotalSize());
    }
  }

  private void handleBulkData(final int type,@Nullable NbtCompound nbt) {
    if(nbt==null||nbt.isEmpty()) {
      return;
    }

    String task=nbt.getString("Task","BulkEntityReply");

    // For future Granular Task Management
    switch(task) {
      // File-Transmit support
      case "Boxmatic-TransmitStart","Boxmatic-TransmitCancel","Boxmatic-TransmitData","Boxmatic-TransmitEnd"-> {
        Pair<BoxmaticaSchematic,NbtCompound> schemPair=BoxmaticaSchematic.receiveFileTransmit(nbt);

        if(schemPair!=null&&schemPair.getLeft().getFile()!=null) {
          Boxmatica.LOGGER.info("handleBulkData(): Received boxmatic '{}' from the server",schemPair.getLeft().getFile().toAbsolutePath().toString());

          SchematicPlacement placement=SchematicPlacement.createFromNbt(schemPair.getLeft(),schemPair.getRight());

          if(placement!=null) {
            DataManager.getSchematicPlacementManager().addSchematicPlacement(placement,true);
          }
        }
      }
      default->EntitiesDataStorage.getInstance().handleBulkEntityData(type,nbt);
    }
  }

  @Override
  public void reset(Identifier channel) {
    if(channel.equals(CHANNEL_ID)&&this.servuxRegistered) {
      this.servuxRegistered=false;
      this.failures=0;
      this.readingSessionKey=-1;
    }
  }

  public void resetFailures(Identifier channel) {
    if(channel.equals(CHANNEL_ID)&&this.failures>0) {
      this.failures=0;
    }
  }

  @Override
  public void receivePlayPayload(T payload,ClientPlayNetworking.Context ctx) {
    if(payload.getId().id().equals(CHANNEL_ID)) {
      ServuxBoxmaticaHandler.INSTANCE.decodeClientData(CHANNEL_ID,((ServuxBoxmaticaPacket.Payload)payload).data());
    }
  }

  @Override
  public void encodeWithSplitter(PacketByteBuf buffer,ClientPlayNetworkHandler handler) {
    // Send each PacketSplitter buffer slice
    ServuxBoxmaticaHandler.INSTANCE.sendPlayPayload(new ServuxBoxmaticaPacket.Payload(ServuxBoxmaticaPacket.ResponseC2SData(buffer)));
  }

  @Override
  public <P extends IClientPayloadData> void encodeClientData(P data) {
    ServuxBoxmaticaPacket packet=(ServuxBoxmaticaPacket)data;

    if(packet.getType().equals(ServuxBoxmaticaPacket.Type.PACKET_C2S_NBT_RESPONSE_START)) {
      PacketByteBuf buffer=new PacketByteBuf(Unpooled.buffer());
      buffer.writeVarInt(packet.getTransactionId());
      buffer.writeNbt(packet.getCompound());
      PacketSplitter.send(this,buffer,MinecraftClient.getInstance().getNetworkHandler());
    }else if(!ServuxBoxmaticaHandler.INSTANCE.sendPlayPayload(new ServuxBoxmaticaPacket.Payload(packet))) {
      if(this.failures>MAX_FAILURES) {
        Boxmatica.LOGGER.warn("encodeClientData(): encountered [{}] sendPayload failures, cancelling any Servux join attempt(s)",MAX_FAILURES);
        this.servuxRegistered=false;
        ServuxBoxmaticaHandler.INSTANCE.unregisterPlayReceiver();
        EntitiesDataStorage.getInstance().onPacketFailure();
      }else {
        this.failures++;
      }
    }
  }
}
