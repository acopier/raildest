package io.github.acopier.raildest.switches;

import com.cjcrafter.foliascheduler.ServerImplementation;
import com.cjcrafter.foliascheduler.TaskImplementation;
import com.cjcrafter.foliascheduler.util.ServerVersions;
import com.google.common.base.Strings;
import io.github.acopier.raildest.RailDestPlugin;
import io.github.acopier.raildest.utilities.DestinationData;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Switch listener that implements switch functionality and uses FoliaScheduler's
 * ServerImplementation to run region-bound work on the correct thread.
 */
public class SwitchListener implements Listener {

  public static final String WILDCARD = "*";
  private static final RailDestPlugin plugin = RailDestPlugin.getPlugin();

  private static final ServerImplementation scheduler = plugin.getScheduler();

  @EventHandler
  public void onSwitchTrigger(final BlockRedstoneEvent event) {
    final Block block = event.getBlock();

    if (block.getType() != Material.DETECTOR_RAIL || event.getNewCurrent() != 15) {
      return;
    }

    final Location location = block.getLocation();

    // If we're already running on the region thread owning this block, run directly.
    if (scheduler.isOwnedByCurrentRegion(block)) {
      handleSwitchEvent(event, block);
      return;
    }

    scheduler.region(location).run((Consumer<TaskImplementation<Void>>) regionTask -> handleSwitchEvent(event, block));
  }

  private void handleSwitchEvent(final BlockRedstoneEvent event, final Block block) {
    // Check that the block above the rail is a sign
    final Block above = block.getRelative(BlockFace.UP);
    if (!Tag.SIGNS.isTagged(above.getType()) || !(above.getState() instanceof Sign)) {
      return;
    }

    final PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
    final String[] lines = ((Sign) above.getState()).getSide(Side.FRONT)
        .lines()
        .stream()
        .map(serializer::serialize)
        .toArray(String[]::new);

    // Check that the sign has a valid switch type
    final SwitchType type = SwitchType.find(lines[0]);
    if (type == null) {
      return;
    }

    // Check that a player is triggering the switch
    Player player = null;
    {
      double searchDistance = Double.MAX_VALUE;
      for (final Entity entity : block.getWorld().getNearbyEntities(block.getLocation(), 3, 3, 3)) {
        if (!(entity instanceof Player)) {
          continue;
        }
        final Entity vehicle = entity.getVehicle();
        if (vehicle == null || vehicle.getType() != EntityType.MINECART || !(vehicle instanceof Minecart)) {
          continue;
        }
        final double distance = block.getLocation().distanceSquared(entity.getLocation());
        if (distance < searchDistance) {
          searchDistance = distance;
          player = (Player) entity;
        }
      }
    }
    if (player == null) {
      return;
    }

    boolean matched = false;
    final String setDestination = DestinationData.getDestination(player);
    if (!Strings.isNullOrEmpty(setDestination)) {
      final String[] playerdestination = setDestination.split(" ");
      final String[] switchdestination = Arrays.copyOfRange(lines, 1, lines.length);
      matcher:
      for (final String playerDestination : playerdestination) {
        if (Strings.isNullOrEmpty(playerDestination)) {
          continue;
        }
        if (playerDestination.equals(WILDCARD)) {
          matched = true;
          break;
        }
        for (final String switchDestination : switchdestination) {
          if (Strings.isNullOrEmpty(switchDestination)) {
            continue;
          }
          if (switchDestination.equals(WILDCARD) || playerDestination.equalsIgnoreCase(switchDestination)) {
            matched = true;
            break matcher;
          }
        }
      }
    }

    // Folia somehow inverts the junction, so we invert the direction to fix it
    if (ServerVersions.isFolia()) {
      matched = !matched;
    }
    switch (type) {
      case NORMAL:
        event.setNewCurrent(matched ? 15 : 0);
        break;
      case INVERTED:
        event.setNewCurrent(matched ? 0 : 15);
        break;
      default:
        // no-op
    }
  }
}
