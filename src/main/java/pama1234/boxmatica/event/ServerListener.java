package pama1234.boxmatica.event;

import net.minecraft.server.integrated.IntegratedServer;
import fi.dy.masa.malilib.interfaces.IServerListener;
import pama1234.boxmatica.data.DataManager;

public class ServerListener implements IServerListener{
  @Override
  public void onServerIntegratedSetup(IntegratedServer server) {
    DataManager.getInstance().setHasIntegratedServer(true);
  }
}
