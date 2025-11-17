package io.github.acopier.raildest.utilities;

import io.github.acopier.raildest.RailDestPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class DestinationData {

  private static final NamespacedKey KEY =
      new NamespacedKey(RailDestPlugin.PLUGIN_ID, "destination");

  public static void setDestination(Player player, String destination) {
    if (destination == null || destination.isEmpty()) {
      player.getPersistentDataContainer().remove(KEY);
      return;
    }
    player.getPersistentDataContainer().set(KEY, PersistentDataType.STRING, destination);
  }

  public static String getDestination(Player player) {
    return player.getPersistentDataContainer().getOrDefault(KEY, PersistentDataType.STRING, "");
  }
}