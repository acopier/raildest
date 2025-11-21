package io.github.acopier.raildest.commands;

import com.cjcrafter.foliascheduler.util.ServerVersions;
import com.google.common.base.Strings;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.acopier.raildest.RailDestPlugin;
import io.github.acopier.raildest.utilities.DestinationData;
import io.github.acopier.raildest.utilities.PrettyMessage;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class DestinationCommand {
  private static final String commandName = "dest";

  private static final RailDestPlugin plugin = RailDestPlugin.getPlugin();

  // /dest [destination]
  public static LiteralArgumentBuilder<CommandSourceStack> createCommand() {
    return Commands.literal(commandName).executes(context -> executeCommand(context, null))
        .then(Commands.literal("unset").executes(context -> executeCommand(context, "unset")))
        .then(Commands.literal("info").executes(context -> executeCommand(context, "info")))
        .then(Commands.argument("destination", StringArgumentType.greedyString())
            .executes(context -> executeCommand(context, StringArgumentType.getString(context, "destination"))));
  }

  private static int executeCommand(CommandContext<CommandSourceStack> context, @Nullable String destination) {
    CommandSourceStack source = context.getSource();
    Object executor = source.getExecutor();

    if (!(executor instanceof Player player)) {
      source.getSender().sendMessage(PrettyMessage.info(String.format("Only players can invoke /%s", commandName)));
      return -Command.SINGLE_SUCCESS;
    }

    if (Strings.isNullOrEmpty(destination)) {
      String currentDestination = DestinationData.getDestination(player);
      // guard against null
      if (currentDestination.isEmpty()) {
        player.sendMessage(PrettyMessage.error("You don't have a destination set"));
      } else {
        player.sendMessage(PrettyMessage.info(String.format("Your current destination is: %s", currentDestination)));
      }
      return Command.SINGLE_SUCCESS;
    }

    switch (destination) {
      case "unset":
        DestinationData.setDestination(player, null);
        player.sendMessage(PrettyMessage.info("Destination unset"));
        break;
      case "info":
        player.sendMessage(PrettyMessage.info("Information")
            .appendNewline()
            .append(Component.text("[-] Version: ", NamedTextColor.GOLD), Component.text(plugin.PLUGIN_VERSION, NamedTextColor.GREEN))
            .appendNewline()
            .append(Component.text("[-] Folia: ", NamedTextColor.GOLD), Component.text(ServerVersions.isFolia(), NamedTextColor.GREEN)));
        break;
      default:
        DestinationData.setDestination(player, destination);
        player.sendMessage(PrettyMessage.info(String.format("Destination set to: %s", destination)));
        break;
    }
    return Command.SINGLE_SUCCESS;
  }
}
