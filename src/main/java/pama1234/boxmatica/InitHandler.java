package pama1234.boxmatica;

import net.minecraft.client.MinecraftClient;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.*;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.data.ModInfo;
import pama1234.boxmatica.config.Configs;
import pama1234.boxmatica.data.DataManager;
import pama1234.boxmatica.data.EntitiesDataStorage;
import pama1234.boxmatica.event.*;
import pama1234.boxmatica.gui.GuiConfigs;
import pama1234.boxmatica.render.infohud.StatusInfoRenderer;
import pama1234.boxmatica.scheduler.ClientTickHandler;

public class InitHandler implements IInitializationHandler{
  @Override
  public void registerModHandlers() {
    ConfigManager.getInstance().registerConfigHandler(Reference.MOD_ID,new Configs());
    Registry.CONFIG_SCREEN.registerConfigScreenFactory(
      new ModInfo(Reference.MOD_ID,Reference.MOD_NAME,GuiConfigs::new));

    EntitiesDataStorage.getInstance().onGameInit();

    InputEventHandler.getKeybindManager().registerKeybindProvider(InputHandler.getInstance());
    InputEventHandler.getInputManager().registerKeyboardInputHandler(InputHandler.getInstance());
    InputEventHandler.getInputManager().registerMouseInputHandler(InputHandler.getInstance());

    IRenderer renderer=new RenderHandler();
    RenderEventHandler.getInstance().registerGameOverlayRenderer(renderer);
    RenderEventHandler.getInstance().registerWorldPreWeatherRenderer(renderer);
    RenderEventHandler.getInstance().registerWorldLastRenderer(renderer);

    ServerHandler.getInstance().registerServerHandler(new ServerListener());

    TickHandler.getInstance().registerClientTickHandler(new ClientTickHandler());
    TickHandler.getInstance().registerClientTickHandler(EntitiesDataStorage.getInstance());

    WorldLoadListener listener=new WorldLoadListener();
    WorldLoadHandler.getInstance().registerWorldLoadPreHandler(listener);
    WorldLoadHandler.getInstance().registerWorldLoadPostHandler(listener);

    KeyCallbacks.init(MinecraftClient.getInstance());
    StatusInfoRenderer.init();

    DataManager.getAreaSelectionsBaseDirectory();
    DataManager.getSchematicsBaseDirectory();
  }
}
