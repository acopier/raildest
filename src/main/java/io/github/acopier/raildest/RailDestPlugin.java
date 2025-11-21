package io.github.acopier.raildest;

import io.github.acopier.raildest.commands.DestinationCommand;
import io.github.acopier.raildest.schedulers.base.TaskScheduler;
import io.github.acopier.raildest.schedulers.folia.FoliaTaskScheduler;
import io.github.acopier.raildest.schedulers.paper.PaperTaskScheduler;
import io.github.acopier.raildest.switches.SwitchListener;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class RailDestPlugin extends JavaPlugin {
  public static final String PLUGIN_ID = "raildest";
  private static RailDestPlugin plugin;
  public final String PLUGIN_VERSION = getPluginMeta().getVersion();
  private final TaskScheduler scheduler;

  public RailDestPlugin() {
    plugin = this;
    this.scheduler = isFolia() ? new FoliaTaskScheduler(this) :
        new PaperTaskScheduler(this);
  }

  public static boolean classExists(@NotNull String className) {
    try {
      Class.forName(className);
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  public static RailDestPlugin getPlugin() {
    return plugin;
  }

  public boolean isFolia() {
    return classExists("io.papermc.paper.threadedregions.RegionizedServer");
  }

  @Override
  public void onEnable() {
    // command registration
    this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
        commands -> {
          commands.registrar().register(DestinationCommand.createCommand().build());
        });
    // event registration
    getServer().getPluginManager().registerEvents(new SwitchListener(),
        this);
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }

  public TaskScheduler getScheduler() {
    return scheduler;
  }
}
