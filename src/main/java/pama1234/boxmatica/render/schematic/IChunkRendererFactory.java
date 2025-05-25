package pama1234.boxmatica.render.schematic;

import pama1234.boxmatica.world.WorldSchematic;

public interface IChunkRendererFactory{
  ChunkRendererSchematicVbo create(WorldSchematic worldIn,WorldRendererSchematic worldRenderer);
}
