package io.github.acopier.raildest.utilities

import io.github.acopier.raildest.RailDest
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType


object DestinationData {
    private val KEY = RailDest.plugin?.let {
        NamespacedKey(
            it, "destination"
        )
    }

    fun setDestination(player: Player, destination: String?) {
        if (destination == null || destination.isEmpty()) {
            KEY?.let { player.persistentDataContainer.remove(it) }
            return
        }
        KEY?.let {
            player.persistentDataContainer.set(
                it,
                PersistentDataType.STRING,
                destination
            )
        }
    }

    fun getDestination(player: Player): String? {
        return KEY?.let {
            player.persistentDataContainer.getOrDefault(
                it,
                PersistentDataType.STRING,
                ""
            )
        }
    }
}
