package pama1234.boxmatica.gui.widgets;

import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import pama1234.boxmatica.data.SchematicHolder;
import pama1234.boxmatica.gui.Icons;
import pama1234.boxmatica.schematic.BoxmaticaSchematic;
import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetSearchBar;
import fi.dy.masa.malilib.util.FileUtils;

public class WidgetListLoadedSchematics extends WidgetListBase<BoxmaticaSchematic,WidgetSchematicEntry>{
  public WidgetListLoadedSchematics(int x,int y,int width,int height,
    @Nullable ISelectionListener<BoxmaticaSchematic> selectionListener) {
    super(x,y,width,height,selectionListener);

    this.browserEntryHeight=22;
    this.widgetSearchBar=new WidgetSearchBar(x+2,y+4,width-14,14,0,Icons.FILE_ICON_SEARCH,LeftRight.LEFT);
    this.browserEntriesOffsetY=this.widgetSearchBar.getHeight()+3;
  }

  @Override
  protected Collection<BoxmaticaSchematic> getAllEntries() {
    return SchematicHolder.getInstance().getAllSchematics();
  }

  @Override
  protected List<String> getEntryStringsForFilter(BoxmaticaSchematic entry) {
    String metaName=entry.getMetadata().getName().toLowerCase();

    if(entry.getFile()!=null) {
      String fileName=FileUtils.getNameWithoutExtension(entry.getFile().getFileName().toString().toLowerCase());
      return ImmutableList.of(metaName,fileName);
    }else {
      return ImmutableList.of(metaName);
    }
  }

  @Override
  protected WidgetSchematicEntry createListEntryWidget(int x,int y,int listIndex,boolean isOdd,BoxmaticaSchematic entry) {
    return new WidgetSchematicEntry(x,y,this.browserEntryWidth,this.getBrowserEntryHeightFor(entry),
      isOdd,entry,listIndex,this);
  }
}
