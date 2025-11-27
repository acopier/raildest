package io.github.acopier.raildest

import com.cjcrafter.foliascheduler.FoliaCompatibility
import com.cjcrafter.foliascheduler.ServerImplementation
import io.github.acopier.raildest.commands.DestinationCommand.createCommand
import io.github.acopier.raildest.switches.SwitchListener
import io.papermc.paper.plugin.lifecycle.event.handler.LifecycleEventHandler
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.java.JavaPlugin

class RailDestPlugin : JavaPlugin() {
  val pluginVersion: String = pluginMeta.version

  /**
   * Getter for foliascheduler ServerImplementation instance.
   * SwitchListener and other code should reuse this same instance.
   */
  init {
    plugin = this
  }

  val scheduler: ServerImplementation =
    FoliaCompatibility(this).serverImplementation

  override fun onEnable() {
    // command registration
    this.lifecycleManager.registerEventHandler(
      LifecycleEvents.COMMANDS, LifecycleEventHandler { commands ->
        commands.registrar().register(
          createCommand().build()
        )
      })
    // event registration
    server.pluginManager.registerEvents(SwitchListener(), this)
  }

  override fun onDisable() {
    // Plugin shutdown logic
  }

  companion object {
    var plugin: RailDestPlugin? = null
  }
}