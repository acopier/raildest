package io.github.acopier.raildest.utilities

import io.github.acopier.raildest.RailDest
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver


object GameLogger {
    private const val PREFIX = "<gold>[RailDest]</gold>"

    fun info(text: String, vararg resolvers: TagResolver): Component {
        return RailDest.miniMessage.deserialize("$PREFIX <green>$text</green>", TagResolver.resolver(*resolvers))
    }

    fun error(text: String): Component {
        return RailDest.miniMessage.deserialize("$PREFIX <red>$text</red>")
    }
}
