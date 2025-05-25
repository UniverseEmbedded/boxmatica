package pama1234.boxmatica.scheduler.tasks;

import java.nio.file.Path;
import java.util.*;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableMap;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.IntBoundingBox;
import pama1234.boxmatica.data.EntitiesDataStorage;
import pama1234.boxmatica.data.SchematicHolder;
import pama1234.boxmatica.render.infohud.InfoHud;
import pama1234.boxmatica.schematic.BoxmaticaSchematic;
import pama1234.boxmatica.selection.AreaSelection;
import pama1234.boxmatica.selection.Box;
import pama1234.boxmatica.util.PositionUtils;

public class TaskSaveSchematic extends TaskProcessChunkBase{
  private final BoxmaticaSchematic schematic;
  private final BlockPos origin;
  private final ImmutableMap<String,Box> subRegions;
  private final Set<UUID> existingEntities=new HashSet<>();
  @Nullable
  private final Path dir;
  @Nullable
  private final String fileName;
  private final BoxmaticaSchematic.SchematicSaveInfo info;
  private final boolean overrideFile;
  protected final boolean fromSchematicWorld;

  public TaskSaveSchematic(BoxmaticaSchematic schematic,AreaSelection area,BoxmaticaSchematic.SchematicSaveInfo info) {
    this(null,null,schematic,area,info,false);
  }

  public TaskSaveSchematic(@Nullable Path dir,@Nullable String fileName,BoxmaticaSchematic schematic,AreaSelection area,BoxmaticaSchematic.SchematicSaveInfo info,boolean overrideFile) {
    super("boxmatica.gui.label.task_name.save_schematic");

    this.dir=dir;
    this.fileName=fileName;
    this.schematic=schematic;
    this.origin=area.getEffectiveOrigin();
    this.subRegions=area.getAllSubRegions();
    this.info=info;
    this.overrideFile=overrideFile;
    this.fromSchematicWorld=info.fromSchematicWorld;

    this.addPerChunkBoxes(area.getAllSubRegionBoxes());
  }

  @Override
  protected boolean canProcessChunk(ChunkPos pos) {
    if(this.fromSchematicWorld) {
      return this.schematicWorld!=null&&this.schematicWorld.getChunkManager().isChunkLoaded(pos.x,pos.z);
    }

    // Request entity data from Servux, if the ClientWorld matches, and treat it as not yet loaded
    if((EntitiesDataStorage.getInstance().hasServuxServer()||
      EntitiesDataStorage.getInstance().getIfReceivedBackupPackets())&&
      Objects.equals(EntitiesDataStorage.getInstance().getWorld(),this.clientWorld)) {
      if(EntitiesDataStorage.getInstance().hasCompletedChunk(pos)) {
        return this.areSurroundingChunksLoaded(pos,this.clientWorld,0);
      }else if(EntitiesDataStorage.getInstance().hasPendingChunk(pos)==false) {
        ImmutableMap<String,IntBoundingBox> volumes=PositionUtils.getBoxesWithinChunk(pos.x,pos.z,this.subRegions);
        int minY=319; // Invert Values
        int maxY=-60;

        for(Map.Entry<String,IntBoundingBox> volumeEntry:volumes.entrySet()) {
          IntBoundingBox bb=volumeEntry.getValue();

          minY=Math.min(bb.minY,minY);
          maxY=Math.max(bb.maxY,maxY);
        }

        if(EntitiesDataStorage.getInstance().hasServuxServer()) {
          EntitiesDataStorage.getInstance().requestServuxBulkEntityData(pos,minY,maxY);
        }else if(EntitiesDataStorage.getInstance().getIfReceivedBackupPackets()) {
          EntitiesDataStorage.getInstance().requestBackupBulkEntityData(pos,minY,maxY);
        }
      }

      return false;
    }

    return this.areSurroundingChunksLoaded(pos,this.clientWorld,0);
  }

  @Override
  protected boolean processChunk(ChunkPos pos) {
    World world=this.fromSchematicWorld?this.schematicWorld:this.world;
    ImmutableMap<String,IntBoundingBox> volumes=PositionUtils.getBoxesWithinChunk(pos.x,pos.z,this.subRegions);
    this.schematic.takeBlocksFromWorldWithinChunk(world,volumes,this.subRegions,this.info);

    if(this.info.ignoreEntities==false) {
      this.schematic.takeEntitiesFromWorldWithinChunk(world,pos.x,pos.z,volumes,this.subRegions,this.existingEntities,this.origin);
    }

    if((EntitiesDataStorage.getInstance().hasServuxServer()||
      EntitiesDataStorage.getInstance().getIfReceivedBackupPackets())&&
      EntitiesDataStorage.getInstance().hasCompletedChunk(pos)&&
      Objects.equals(EntitiesDataStorage.getInstance().getWorld(),this.clientWorld)) {
      EntitiesDataStorage.getInstance().markCompletedChunkDirty(pos);
      // Mark Dirty for refresh
    }

    return true;
  }

  @Override
  protected void onStop() {
    if(this.finished) {
      long time=System.currentTimeMillis();
      this.schematic.getMetadata().setTimeCreated(time);
      this.schematic.getMetadata().setTimeModified(time);
      this.schematic.getMetadata().setTotalBlocks(this.schematic.getTotalBlocksReadFromWorld());

      if(this.dir!=null) {
        if(this.schematic.writeToFile(this.dir,this.fileName,this.overrideFile)) {
          if(this.printCompletionMessage) {
            InfoUtils.showGuiOrInGameMessage(MessageType.SUCCESS,"boxmatica.message.schematic_saved_as",this.fileName);
          }
        }else {
          InfoUtils.showGuiOrInGameMessage(MessageType.ERROR,"boxmatica.message.error.schematic_save_failed",this.fileName);
        }
      }
      // In-memory only
      else {
        String name=this.schematic.getMetadata().getName();
        SchematicHolder.getInstance().addSchematic(this.schematic,true);

        if(this.printCompletionMessage) {
          InfoUtils.showGuiOrInGameMessage(MessageType.SUCCESS,"boxmatica.message.in_memory_schematic_created",name);
        }
      }
    }else {
      InfoUtils.showGuiOrInGameMessage(MessageType.WARNING,"boxmatica.message.error.schematic_save_interrupted");
    }

    InfoHud.getInstance().removeInfoHudRenderer(this,false);

    this.notifyListener();
  }
}
