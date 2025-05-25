package pama1234.boxmatica.schematic.container;

import net.minecraft.block.BlockState;

public interface IBoxmaticaBlockStatePaletteResizer{
  int onResize(int bits,BlockState state);
}
