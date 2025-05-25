package pama1234.boxmatica.schematic.container;

import java.util.List;
import javax.annotation.Nullable;
import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtList;

public interface IBoxmaticaBlockStatePalette{
  @ApiStatus.Experimental
  Codec<? extends IBoxmaticaBlockStatePalette> codec();

  void setResizer(IBoxmaticaBlockStatePaletteResizer resizer);

  /**
   * Gets the palette id for the given block state and adds
   * the state to the palette if it doesn't exist there yet.
   */
  int idFor(BlockState state);

  /**
   * Gets the block state by the palette id.
   */
  @Nullable
  BlockState getBlockState(int indexKey);

  int getPaletteSize();

  void readFromNBT(NbtList tagList);

  NbtList writeToNBT();

  /**
   * Sets the current mapping of the palette.
   * This is meant for reading the palette from file.
   * 
   * @param list
   * @return true if the mapping was set successfully, false if it failed
   */
  boolean setMapping(List<BlockState> list);

  List<BlockState> fromMapping();
}
