package pama1234.boxmatica.gui;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import pama1234.boxmatica.Reference;
import pama1234.boxmatica.config.Configs;
import pama1234.boxmatica.config.Hotkeys;
import pama1234.boxmatica.data.DataManager;
import pama1234.boxmatica.util.SchematicWorldRefresher;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;

public class GuiConfigs extends GuiConfigsBase{
  public GuiConfigs() {
    super(10,50,Reference.MOD_ID,null,"boxmatica.gui.title.configs",String.format("%s",Reference.MOD_VERSION));
  }

  @Override
  public void initGui() {
    super.initGui();
    this.clearOptions();

    if(DataManager.getConfigGuiTab()==ConfigGuiTab.RENDER_LAYERS) {
      GuiBase.openGui(new GuiRenderLayer());
      return;
    }

    int x=10;
    int y=26;

    x+=this.createButton(x,y,-1,ConfigGuiTab.GENERIC);
    x+=this.createButton(x,y,-1,ConfigGuiTab.INFO_OVERLAYS);
    x+=this.createButton(x,y,-1,ConfigGuiTab.VISUALS);
    x+=this.createButton(x,y,-1,ConfigGuiTab.COLORS);
    x+=this.createButton(x,y,-1,ConfigGuiTab.HOTKEYS);
    x+=this.createButton(x,y,-1,ConfigGuiTab.RENDER_LAYERS);
    //x += this.createButton(x, y, -1, ConfigGuiTab.TEST);
  }

  private int createButton(int x,int y,int width,ConfigGuiTab tab) {
    ButtonGeneric button=new ButtonGeneric(x,y,width,20,tab.getDisplayName());
    button.setEnabled(DataManager.getConfigGuiTab()!=tab);
    this.addButton(button,new ButtonListener(tab,this));

    return button.getWidth()+2;
  }

  @Override
  protected int getConfigWidth() {
    ConfigGuiTab tab=DataManager.getConfigGuiTab();

    if(tab==ConfigGuiTab.GENERIC||tab==ConfigGuiTab.INFO_OVERLAYS||tab==ConfigGuiTab.VISUALS) {
      return 140;
    }
    if(tab==ConfigGuiTab.COLORS) {
      return 100;
    }

    return super.getConfigWidth();
  }

  @Override
  protected boolean useKeybindSearch() {
    return DataManager.getConfigGuiTab()==ConfigGuiTab.HOTKEYS;
  }

  @Override
  public List<ConfigOptionWrapper> getConfigs() {
    List<? extends IConfigBase> configs;
    ConfigGuiTab tab=DataManager.getConfigGuiTab();
    configs=switch(tab) {
      case GENERIC->Configs.Generic.OPTIONS;
      case INFO_OVERLAYS->Configs.InfoOverlays.OPTIONS;
      case VISUALS->Configs.Visuals.OPTIONS;
      case COLORS->Configs.Colors.OPTIONS;
      case HOTKEYS->Hotkeys.HOTKEY_LIST;
      case RENDER_LAYERS->Collections.emptyList();
      //case TEST -> Configs.Test.OPTIONS;
    };
    return ConfigOptionWrapper.createFor(configs);
  }

  @Override
  protected void onSettingsChanged() {
    super.onSettingsChanged();

    SchematicWorldRefresher.INSTANCE.updateAll();
  }

  private record ButtonListener(ConfigGuiTab tab,GuiConfigs parent) implements IButtonActionListener {

    @Override
    public void actionPerformedWithButton(ButtonBase button,int mouseButton) {
      DataManager.setConfigGuiTab(this.tab);

      if(this.tab!=ConfigGuiTab.RENDER_LAYERS) {
        this.parent.reCreateListWidget(); // apply the new config width
        Objects.requireNonNull(this.parent.getListWidget()).resetScrollbarPosition();
        this.parent.initGui();
      }else {
        GuiBase.openGui(new GuiRenderLayer());
      }
    }
  }

  public enum ConfigGuiTab{
    GENERIC("boxmatica.gui.button.config_gui.generic"),
    INFO_OVERLAYS("boxmatica.gui.button.config_gui.info_overlays"),
    VISUALS("boxmatica.gui.button.config_gui.visuals"),
    COLORS("boxmatica.gui.button.config_gui.colors"),
    HOTKEYS("boxmatica.gui.button.config_gui.hotkeys"),
    RENDER_LAYERS("boxmatica.gui.button.config_gui.render_layers");
    //TEST            ("boxmatica.gui.button.config_gui.test");

    private final String translationKey;

    private ConfigGuiTab(String translationKey) {
      this.translationKey=translationKey;
    }

    public String getDisplayName() {
      return StringUtils.translate(this.translationKey);
    }
  }
}
