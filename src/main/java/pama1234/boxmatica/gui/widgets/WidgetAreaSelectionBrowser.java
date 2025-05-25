package pama1234.boxmatica.gui.widgets;

import java.nio.file.Path;

import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase;
import pama1234.boxmatica.data.DataManager;
import pama1234.boxmatica.gui.GuiAreaSelectionManager;
import pama1234.boxmatica.gui.Icons;

public class WidgetAreaSelectionBrowser extends WidgetFileBrowserBase{
  public static final FileFilter JSON_FILTER=new FileFilterJson();

  private final GuiAreaSelectionManager guiAreaSelectionManager;

  public WidgetAreaSelectionBrowser(int x,int y,int width,int height,
    GuiAreaSelectionManager parent,ISelectionListener<DirectoryEntry> selectionListener) {
    super(x,y,width,height,DataManager.getDirectoryCache(),parent.getBrowserContext(),
      parent.getDefaultDirectory(),selectionListener,Icons.DUMMY);

    this.browserEntryHeight=22;
    this.guiAreaSelectionManager=parent;
    this.allowKeyboardNavigation=false;
  }

  public GuiAreaSelectionManager getSelectionManagerGui() {
    return this.guiAreaSelectionManager;
  }

  @Override
  protected Path getRootDirectory() {
    return DataManager.getAreaSelectionsBaseDirectory();
  }

  @Override
  protected FileFilter getFileFilter() {
    return JSON_FILTER;
  }

  @Override
  protected WidgetAreaSelectionEntry createListEntryWidget(int x,int y,int listIndex,boolean isOdd,DirectoryEntry entry) {
    return new WidgetAreaSelectionEntry(x,y,this.browserEntryWidth,this.getBrowserEntryHeightFor(entry),isOdd,
      entry,listIndex,this.guiAreaSelectionManager.getSelectionManager(),this,this.iconProvider);
  }

  /*
   * public static class FileFilterJson implements FileFilter
   * {
   * 
   * @Override
   * public boolean accept(File pathName)
   * {
   * return pathName.getName().endsWith(".json");
   * }
   * }
   */

  public static class FileFilterJson extends FileFilter{
    @Override
    public boolean accept(Path entry) {
      return entry.getFileName().toString().endsWith(".json");
    }
  }
}
