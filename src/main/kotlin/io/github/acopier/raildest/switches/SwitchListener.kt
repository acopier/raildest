package io.github.acopier.raildest.switches

import com.cjcrafter.foliascheduler.util.ServerVersions
import com.google.common.base.Strings
import io.github.acopier.raildest.RailDestPlugin
import io.github.acopier.raildest.utilities.DestinationData
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.Sign
import org.bukkit.block.sign.Side
import org.bukkit.entity.EntityType
import org.bukkit.entity.Minecart
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockRedstoneEvent


/**
 * Switch listener that implements switch functionality and uses FoliaScheduler's
 * ServerImplementation to run region-bound work on the correct thread.
 */
class SwitchListener : Listener {
  @EventHandler
  fun onSwitchTrigger(event: BlockRedstoneEvent) {
    val block = event.getBlock()

    if (block.type != Material.DETECTOR_RAIL || event.newCurrent != 15) {
      return
    }

    val location = block.location

    // If we're already running on the region thread owning this block, run directly.
    scheduler?.let {
      if (it.isOwnedByCurrentRegion(block)) {
        handleSwitchEvent(event, block)
        return
      }
    }

    scheduler?.region(location)?.run { _ ->
      handleSwitchEvent(
        event, block
      )
    }
  }

  private fun handleSwitchEvent(event: BlockRedstoneEvent, block: Block) {
    // Check that the block above the rail is a sign
    val above = block.getRelative(BlockFace.UP)
    if (!Tag.SIGNS.isTagged(above.type) || above.state !is Sign) {
      return
    }

    val serializer = PlainTextComponentSerializer.plainText()
    val lines = (above.state as Sign).getSide(Side.FRONT).lines().stream()
      .map<String> { component -> serializer.serialize(component) }.toArray()

    // Check that the sign has a valid switch type
    val type = SwitchType.find(lines[0] as String?) ?: return

    // Check that a player is triggering the switch
    var player: Player? = null
    run {
      var searchDistance = Double.MAX_VALUE
      for (entity in block.world.getNearbyEntities(
        block.location, 3.0, 3.0, 3.0
      )) {
        if (entity !is Player) {
          continue
        }
        val vehicle = entity.vehicle
        if (vehicle == null || vehicle.type != EntityType.MINECART || (vehicle !is Minecart)) {
          continue
        }
        val distance = block.location.distanceSquared(entity.location)
        if (distance < searchDistance) {
          searchDistance = distance
          player = entity
        }
      }
    }
    if (player == null) {
      return
    }

    var matched = false
    val setDestination = DestinationData.getDestination(player)
    if (!Strings.isNullOrEmpty(setDestination)) {
      val playerdestination =
        setDestination!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
          .toTypedArray()
      val switchdestination = lines.copyOfRange(1, lines.size)
      matcher@ for (playerDestination in playerdestination) {
        if (Strings.isNullOrEmpty(playerDestination)) {
          continue
        }
        if (playerDestination == WILDCARD) {
          matched = true
          break
        }
        for (switchDestination in switchdestination) {
          if (Strings.isNullOrEmpty(switchDestination as String?)) {
            continue
          }
          if (switchDestination == WILDCARD || playerDestination.equals(
              switchDestination, ignoreCase = true
            )
          ) {
            matched = true
            break@matcher
          }
        }
      }
    }

    // Folia somehow inverts the junction, so we invert the direction to fix it
    if (ServerVersions.isFolia()) {
      matched = !matched
    }
    when (type) {
      SwitchType.NORMAL -> event.newCurrent = if (matched) 15 else 0
      SwitchType.INVERTED -> event.newCurrent = if (matched) 0 else 15
    }
  }

  companion object {
    const val WILDCARD = "*"
    private val plugin = RailDestPlugin.plugin

    private val scheduler = plugin?.scheduler
  }
}
