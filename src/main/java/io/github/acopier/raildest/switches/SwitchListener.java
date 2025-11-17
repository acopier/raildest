package io.github.acopier.raildest.switches;

import com.google.common.base.Strings;
import io.github.acopier.raildest.utilities.DestinationData;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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

/**
 * Switch listener that implements switch functionality.
 */
public class SwitchListener implements Listener {

  public static final String WILDCARD = "*";


  /**
   * Event handler for rail switches. Will determine if a switch exists at the target location, and if so will process
   * it accordingly, allowing it to trigger or not trigger depending on the rider's set destination, the listed
   * destinations on the switch, and the switch type.
   *
   * @param event The block redstone event to base the switch's existence on.
   */
  @EventHandler
  public void onSwitchTrigger(BlockRedstoneEvent event) {
    Block block = event.getBlock();
    // Block must be a detector rail being triggered
    if (block.getType() != Material.DETECTOR_RAIL || event.getNewCurrent() != 15) {
      return;
    }
    // Check that the block above the rail is a sign
    Block above = block.getRelative(BlockFace.UP);
    if (!Tag.SIGNS.isTagged(above.getType()) || !(above.getState() instanceof Sign)) {
      return;
    }
    PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
    String[] lines = ((Sign) above.getState()).getSide(Side.FRONT)
        .lines()
        .stream()
        .map(serializer::serialize)
        .toArray(String[]::new);
    // Check that the sign has a valid switch type
    SwitchType type = SwitchType.find(lines[0]);
    if (type == null) {
      return;
    }
    // Check that a player is triggering the switch
    // NOTE: The event doesn't provide the information and so the next best thing is searching for a
    //       player who is nearby and riding a minecart.
    Player player = null;
    {
      double searchDistance = Double.MAX_VALUE;
      for (Entity entity : block.getWorld().getNearbyEntities(block.getLocation(), 3, 3, 3)) {
        if (!(entity instanceof Player)) {
          continue;
        }
        Entity vehicle = entity.getVehicle();
        if (vehicle == null || vehicle.getType() != EntityType.MINECART || !(vehicle instanceof Minecart)) {
          continue;
        }
        double distance = block.getLocation().distanceSquared(entity.getLocation());
        if (distance < searchDistance) {
          searchDistance = distance;
          player = (Player) entity;
        }
      }
    }
    if (player == null) {
      return;
    }
    // Determine whether a player has a destination that matches one of the destinations
    // listed on the switch signs, or match if there's a wildcard.
    boolean matched = false;
    String setDestination = DestinationData.getDestination(player);
    if (!Strings.isNullOrEmpty(setDestination)) {
      String[] playerdestination = setDestination.split(" ");
      String[] switchdestination = Arrays.copyOfRange(lines, 1, lines.length);
      matcher:
      for (String playerDestination : playerdestination) {
        if (Strings.isNullOrEmpty(playerDestination)) {
          continue;
        }
        if (playerDestination.equals(WILDCARD)) {
          matched = true;
          break;
        }
        for (String switchDestination : switchdestination) {
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
    switch (type) {
      case NORMAL:
        event.setNewCurrent(matched ? 15 : 0);
        break;
      case INVERTED:
        event.setNewCurrent(matched ? 0 : 15);
        break;
    }
  }

}