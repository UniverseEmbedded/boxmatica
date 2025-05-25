package pama1234.boxmatica.world;

import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

import pama1234.boxmatica.Boxmatica;
import pama1234.boxmatica.render.BoxmaticaRenderer;
import pama1234.boxmatica.render.schematic.WorldRendererSchematic;

public class SchematicWorldHandler{
  public static final SchematicWorldHandler INSTANCE=new SchematicWorldHandler(BoxmaticaRenderer.getInstance()::getWorldRenderer);

  protected final Supplier<WorldRendererSchematic> rendererSupplier;
  @Nullable
  protected WorldSchematic world;
  @Nullable
  protected DynamicRegistryManager.Immutable dynamicRegistryManager=DynamicRegistryManager.EMPTY;

  // The supplier can return null, but it can't be null itself!
  public SchematicWorldHandler(Supplier<WorldRendererSchematic> rendererSupplier) {
    this.rendererSupplier=rendererSupplier;
  }

  @Nullable
  public static WorldSchematic getSchematicWorld() {
    return INSTANCE.getWorld();
  }

  @Nullable
  public WorldSchematic getWorld() {
    if(this.world==null) {
      this.world=createSchematicWorld(this.rendererSupplier.get());
    }

    return this.world;
  }

  public void setDynamicRegistryManager(@Nullable DynamicRegistryManager.Immutable immutable) {
    if(immutable==null) {
      return;
    }

    this.dynamicRegistryManager=immutable;
  }

  /**
   * Store/Get the Dynamic Registry if we can get it
   */
  public DynamicRegistryManager getRegistryManager() {
    return this.dynamicRegistryManager;
  }

  public static WorldSchematic createSchematicWorld(@Nullable WorldRendererSchematic worldRenderer) {
    World world=MinecraftClient.getInstance().world;

    if(world==null) {
      return null;
    }

    /*
     * //RegistryEntryLookup.RegistryLookup lookup =
     * world.getRegistryManager().createRegistryLookup();
     * RegistryEntryLookup<DimensionType> entryLookup =
     * SchematicWorldHandler.INSTANCE.getRegistryManager().getOrThrow(RegistryKeys.DIMENSION_TYPE);
     * RegistryEntry<DimensionType> entry = entryLookup.getOrThrow(DimensionTypes.OVERWORLD);
     * 
     * if (entry == null)
     * {
     * entry = world.getDimensionEntry();
     * }
     */
    // Use the DimensionType of the current client world

    ClientWorld.Properties levelInfo=new ClientWorld.Properties(Difficulty.PEACEFUL,false,true);

    return new WorldSchematic(levelInfo,world.getRegistryManager(),world.getDimensionEntry(),worldRenderer);
  }

  public void recreateSchematicWorld(boolean remove) {
    if(remove) {
      Boxmatica.debugLog("Removing the schematic world...");
      if(this.world!=null) {
        this.world.clearEntities();
      }
      this.world=null;
      BoxmaticaRenderer.getInstance().onSchematicWorldChanged(null);
    }else {
      Boxmatica.debugLog("(Re-)creating the schematic world...");
      @Nullable
      WorldRendererSchematic worldRenderer=this.world!=null?this.world.worldRenderer:BoxmaticaRenderer.getInstance().resetWorldRenderer();
      // Note: The dimension used here must have no skylight, because the custom Chunks don't have those arrays
      this.world=createSchematicWorld(worldRenderer);
      Boxmatica.debugLog("Schematic world (re-)created: {}",this.world);
    }

    BoxmaticaRenderer.getInstance().onSchematicWorldChanged(this.world);
  }
}
