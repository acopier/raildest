package io.github.acopier.raildest.utilities

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor


object PrettyMessage {
  private val prefix = Component.text("[RailDest] ", NamedTextColor.GOLD)

  fun info(text: String): TextComponent {
    return prefix.append(Component.text(text, NamedTextColor.GREEN))
  }

  fun error(text: String): TextComponent {
    return prefix.append(Component.text(text, NamedTextColor.RED))
  }
}
