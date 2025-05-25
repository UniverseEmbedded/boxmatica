package pama1234.boxmatica.schematic.container;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import io.netty.buffer.ByteBuf;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.collection.Int2ObjectBiMap;

import pama1234.boxmatica.world.SchematicWorldHandler;

public class BoxmaticaBlockStatePaletteHashMap implements IBoxmaticaBlockStatePalette{
  public static final Codec<BoxmaticaBlockStatePaletteHashMap> CODEC=RecordCodecBuilder.create(
    inst->inst.group(
      PrimitiveCodec.INT.fieldOf("Bits").forGetter(get->get.bits),
      Codec.list(BlockState.CODEC).fieldOf("StatePalette").forGetter(BoxmaticaBlockStatePaletteHashMap::fromMapping)).apply(inst,BoxmaticaBlockStatePaletteHashMap::new));
  public static final PacketCodec<ByteBuf,BoxmaticaBlockStatePaletteHashMap> PACKET_CODEC=new PacketCodec<>() {
    @Override
    public void encode(ByteBuf buf,BoxmaticaBlockStatePaletteHashMap value) {
      PacketCodecs.INTEGER.encode(buf,value.bits);
      PacketCodecs.UNLIMITED_NBT_ELEMENT.encode(buf,value.writeToNBT());
    }

    @Override
    public BoxmaticaBlockStatePaletteHashMap decode(ByteBuf buf) {
      Integer bitsIn=PacketCodecs.INTEGER.decode(buf);
      NbtElement nbt=PacketCodecs.UNLIMITED_NBT_ELEMENT.decode(buf);
      return new BoxmaticaBlockStatePaletteHashMap(bitsIn,(NbtList)nbt);
    }
  };
  private final Int2ObjectBiMap<BlockState> statePaletteMap;
  private IBoxmaticaBlockStatePaletteResizer paletteResizer;
  private final int bits;

  public BoxmaticaBlockStatePaletteHashMap(int bitsIn,IBoxmaticaBlockStatePaletteResizer paletteResizer) {
    this.bits=bitsIn;
    this.paletteResizer=paletteResizer;
    this.statePaletteMap=Int2ObjectBiMap.create(1<<bitsIn);
  }

  private BoxmaticaBlockStatePaletteHashMap(int bitsIn,List<BlockState> list) {
    this.bits=bitsIn;
    this.paletteResizer=null;
    this.statePaletteMap=Int2ObjectBiMap.create(1<<bitsIn);
    this.setMapping(list);
  }

  private BoxmaticaBlockStatePaletteHashMap(int bitsIn,NbtList list) {
    this.bits=bitsIn;
    this.paletteResizer=null;
    this.statePaletteMap=Int2ObjectBiMap.create(1<<bitsIn);
    this.readFromNBT(list);
  }

  @Override
  public Codec<BoxmaticaBlockStatePaletteHashMap> codec() {
    return CODEC;
  }

  @Override
  public void setResizer(IBoxmaticaBlockStatePaletteResizer resizer) {
    this.paletteResizer=resizer;
  }

  @Override
  public int idFor(BlockState state) {
    int i=this.statePaletteMap.getRawId(state);

    if(i==-1) {
      i=this.statePaletteMap.add(state);

      if(i>=(1<<this.bits)) {
        i=this.paletteResizer.onResize(this.bits+1,state);
      }
    }

    return i;
  }

  @Override
  @Nullable
  public BlockState getBlockState(int indexKey) {
    return this.statePaletteMap.get(indexKey);
  }

  @Override
  public int getPaletteSize() {
    return this.statePaletteMap.size();
  }

  private void requestNewId(BlockState state) {
    final int origId=this.statePaletteMap.add(state);

    if(origId>=(1<<this.bits)) {
      int newId=this.paletteResizer.onResize(this.bits+1,BoxmaticaBlockStateContainer.AIR_BLOCK_STATE);

      if(newId<=origId) {
        this.statePaletteMap.add(state);
      }
    }
  }

  @Override
  public void readFromNBT(NbtList tagList) {
    //RegistryEntryLookup<Block> lookup = Registries.BLOCK.getReadOnlyWrapper();
    RegistryEntryLookup<Block> lookup=SchematicWorldHandler.INSTANCE.getRegistryManager().getOrThrow(RegistryKeys.BLOCK);
    // Ugly, but it should work, without changing the IBoxmaticaBlockStatePalette interface.

    final int size=tagList.size();

    for(int i=0;i<size;++i) {
      NbtCompound tag=tagList.getCompoundOrEmpty(i);
      BlockState state=NbtHelper.toBlockState(lookup,tag);

      if(i>0||state!=BoxmaticaBlockStateContainer.AIR_BLOCK_STATE) {
        this.requestNewId(state);
      }
    }
  }

  @Override
  public NbtList writeToNBT() {
    NbtList tagList=new NbtList();

    for(int id=0;id<this.statePaletteMap.size();++id) {
      BlockState state=this.statePaletteMap.get(id);

      if(state==null) {
        state=BoxmaticaBlockStateContainer.AIR_BLOCK_STATE;
      }

      NbtCompound tag=NbtHelper.fromBlockState(state);
      tagList.add(tag);
    }

    return tagList;
  }

  @Override
  public List<BlockState> fromMapping() {
    List<BlockState> list=new ArrayList<>();

    for(int i=0;i<this.statePaletteMap.size();i++) {
      BlockState state=this.statePaletteMap.get(i);

      if(state==null) {
        state=BoxmaticaBlockStateContainer.AIR_BLOCK_STATE;
      }

      list.add(state);
    }

    return list;
  }

  @Override
  public boolean setMapping(List<BlockState> list) {
    this.statePaletteMap.clear();

    for(BlockState blockState:list) {
      this.statePaletteMap.add(blockState);
    }

    return true;
  }
}
