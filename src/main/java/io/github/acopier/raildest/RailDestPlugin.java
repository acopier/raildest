package io.github.acopier.raildest;

import com.cjcrafter.foliascheduler.FoliaCompatibility;
import com.cjcrafter.foliascheduler.ServerImplementation;
import io.github.acopier.raildest.commands.DestinationCommand;
import io.github.acopier.raildest.switches.SwitchListener;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class RailDestPlugin extends JavaPlugin {
  private static RailDestPlugin plugin;
  public final String PLUGIN_VERSION = getPluginMeta().getVersion();

  private final ServerImplementation scheduler;

  public RailDestPlugin() {
    plugin = this;
    this.scheduler = new FoliaCompatibility(this).getServerImplementation();
  }

  public static RailDestPlugin getPlugin() {
    return plugin;
  }

  @Override
  public void onEnable() {
    // command registration
    this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
        commands ->
            commands.registrar().register(DestinationCommand.createCommand().build())
    );
    // event registration
    getServer().getPluginManager().registerEvents(new SwitchListener(), this);
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }

  /**
   * Getter for foliascheduler ServerImplementation instance.
   * SwitchListener and other code should reuse this same instance.
   */
  public ServerImplementation getScheduler() {
    return scheduler;
  }
}
