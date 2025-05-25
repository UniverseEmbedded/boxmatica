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

import pama1234.boxmatica.world.SchematicWorldHandler;

public class BoxmaticaBlockStatePaletteLinear implements IBoxmaticaBlockStatePalette{
  public static final Codec<BoxmaticaBlockStatePaletteLinear> CODEC=RecordCodecBuilder.create(
    inst->inst.group(
      PrimitiveCodec.INT.fieldOf("Bits").forGetter(get->get.bits),
      Codec.list(BlockState.CODEC).fieldOf("StatePalette").forGetter(BoxmaticaBlockStatePaletteLinear::fromMapping)).apply(inst,BoxmaticaBlockStatePaletteLinear::new));
  public static final PacketCodec<ByteBuf,BoxmaticaBlockStatePaletteLinear> PACKET_CODEC=new PacketCodec<>() {
    @Override
    public void encode(ByteBuf buf,BoxmaticaBlockStatePaletteLinear value) {
      PacketCodecs.INTEGER.encode(buf,value.bits);
      PacketCodecs.UNLIMITED_NBT_ELEMENT.encode(buf,value.writeToNBT());
    }

    @Override
    public BoxmaticaBlockStatePaletteLinear decode(ByteBuf buf) {
      Integer bitsIn=PacketCodecs.INTEGER.decode(buf);
      NbtElement nbt=PacketCodecs.UNLIMITED_NBT_ELEMENT.decode(buf);
      return new BoxmaticaBlockStatePaletteLinear(bitsIn,(NbtList)nbt);
    }
  };
  private final BlockState[] states;
  private IBoxmaticaBlockStatePaletteResizer resizeHandler;
  private final int bits;
  private int currentSize;

  public BoxmaticaBlockStatePaletteLinear(int bitsIn,IBoxmaticaBlockStatePaletteResizer resizeHandler) {
    this.states=new BlockState[1<<bitsIn];
    this.bits=bitsIn;
    this.resizeHandler=resizeHandler;
  }

  private BoxmaticaBlockStatePaletteLinear(int bitsIn,List<BlockState> list) {
    this.bits=bitsIn;
    this.resizeHandler=null;
    this.states=new BlockState[1<<bitsIn];
    this.setMapping(list);
  }

  private BoxmaticaBlockStatePaletteLinear(int bitsIn,NbtList list) {
    this.bits=bitsIn;
    this.resizeHandler=null;
    this.states=new BlockState[1<<bitsIn];
    this.readFromNBT(list);
  }

  @Override
  public Codec<BoxmaticaBlockStatePaletteLinear> codec() {
    return CODEC;
  }

  @Override
  public void setResizer(IBoxmaticaBlockStatePaletteResizer resizer) {
    this.resizeHandler=resizer;
  }

  @Override
  public int idFor(BlockState state) {
    for(int i=0;i<this.currentSize;++i) {
      if(this.states[i]==state) {
        return i;
      }
    }

    final int size=this.currentSize;

    if(size<this.states.length) {
      this.states[size]=state;
      ++this.currentSize;
      return size;
    }else {
      return this.resizeHandler.onResize(this.bits+1,state);
    }
  }

  @Override
  @Nullable
  public BlockState getBlockState(int indexKey) {
    return indexKey>=0&&indexKey<this.currentSize?this.states[indexKey]:null;
  }

  @Override
  public int getPaletteSize() {
    return this.currentSize;
  }

  private void requestNewId(BlockState state) {
    final int size=this.currentSize;

    if(size<this.states.length) {
      this.states[size]=state;
      ++this.currentSize;
    }else {
      int newId=this.resizeHandler.onResize(this.bits+1,BoxmaticaBlockStateContainer.AIR_BLOCK_STATE);

      if(newId<=size) {
        this.states[size]=state;
        ++this.currentSize;
      }
    }
  }

  @Override
  public void readFromNBT(NbtList tagList) {
    //RegistryEntryLookup<Block> lookup = Registries.BLOCK.getReadOnlyWrapper();
    RegistryEntryLookup<Block> lookup=SchematicWorldHandler.INSTANCE.getRegistryManager().getOrThrow(RegistryKeys.BLOCK);
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

    for(int id=0;id<this.currentSize;++id) {
      BlockState state=this.states[id];

      if(state==null) {
        state=BoxmaticaBlockStateContainer.AIR_BLOCK_STATE;
      }

      NbtCompound tag=NbtHelper.fromBlockState(state);
      tagList.add(tag);
    }

    return tagList;
  }

  @Override
  public boolean setMapping(List<BlockState> list) {
    final int size=list.size();

    if(size<=this.states.length) {
      for(int id=0;id<size;++id) {
        this.states[id]=list.get(id);
      }

      this.currentSize=size;

      return true;
    }

    return false;
  }

  @Override
  public List<BlockState> fromMapping() {
    List<BlockState> list=new ArrayList<>();

    for(int id=0;id<this.currentSize;++id) {
      BlockState state=this.states[id];

      if(state==null) {
        state=BoxmaticaBlockStateContainer.AIR_BLOCK_STATE;
      }

      list.add(state);
    }

    return list;
  }
}
