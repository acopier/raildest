package io.github.acopier.raildest.utilities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class PrettyMessage {
  private static final TextComponent prefix = Component.text("[RailDest] ", NamedTextColor.GOLD);

  public static TextComponent info(String text) {
    return prefix.append(Component.text(text, NamedTextColor.GREEN));
  }

  public static TextComponent error(String text) {
    return prefix.append(Component.text(text, NamedTextColor.RED));
  }
}
