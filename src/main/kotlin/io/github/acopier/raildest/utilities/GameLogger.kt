package io.github.acopier.raildest.utilities

import io.github.acopier.raildest.RailDest
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage


object GameLogger {
    private val prefix = RailDest.miniMessage.deserialize("<gold>[RailDest] </gold>")

    fun info(text: String): Component {
        return prefix.append(RailDest.miniMessage.deserialize("<green>$text</green>"))
    }

    fun error(text: String): Component {
        return prefix.append(RailDest.miniMessage.deserialize("<red>$text</red>"))
    }
}
