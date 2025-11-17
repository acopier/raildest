package io.github.acopier.raildest;

import io.github.acopier.raildest.commands.DestinationCommand;
import io.github.acopier.raildest.switches.SwitchListener;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class RailDestPlugin extends JavaPlugin implements Listener {
  public static final String PLUGIN_ID = "raildest";

  @Override
  public void onEnable() {
    // command registration
    this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
      commands.registrar().register(DestinationCommand.createCommand().build());
    });
    // event registration
    getServer().getPluginManager().registerEvents(new SwitchListener(), this);
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }
}
