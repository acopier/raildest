package io.github.acopier.raildest.commands

import com.cjcrafter.foliascheduler.util.ServerVersions
import com.google.common.base.Strings
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.github.acopier.raildest.RailDest
import io.github.acopier.raildest.utilities.DestinationData
import io.github.acopier.raildest.utilities.GameLogger
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player


object DestinationCommand {
    private const val COMMAND_NAME = "dest"

    private val plugin = RailDest.plugin

    // /dest [destination]
    fun createCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal(COMMAND_NAME).executes { context ->
            executeCommand(
                context, null
            )
        }.then(
            Commands.literal("unset").executes { context ->
                executeCommand(
                    context, "unset"
                )
            }).then(
            Commands.literal("info").executes { context ->
                executeCommand(
                    context, "info"
                )
            }).then(
            Commands.argument(
                "destination", StringArgumentType.greedyString()
            ).executes { context ->
                executeCommand(
                    context, StringArgumentType.getString(context, "destination")
                )
            })
    }

    private fun executeCommand(
        context: CommandContext<CommandSourceStack>, destination: String?
    ): Int {
        val source = context.getSource()
        val executor = source.executor

        if (executor !is Player) {
            source.sender.sendMessage(
                GameLogger.info(
                    "Only players can invoke /$COMMAND_NAME"
                )
            )
            return -Command.SINGLE_SUCCESS
        }

        if (Strings.isNullOrEmpty(destination)) {
            val currentDestination = DestinationData.getDestination(executor)
            // guard against null
            currentDestination?.let {
                if (it.isEmpty()) {
                    executor.sendMessage(GameLogger.error("You don't have a destination set"))
                } else {
                    executor.sendMessage(
                        GameLogger.info(
                            "Your current destination is: $currentDestination"
                        )
                    )
                }
            }
            return Command.SINGLE_SUCCESS
        }

        when (destination) {
            "unset" -> {
                DestinationData.setDestination(executor, null)
                executor.sendMessage(GameLogger.info("Destination unset"))
            }

            "info" -> executor.sendMessage(
                GameLogger.info("Information").appendNewline().append(
                    Component.text("[-] Version: ", NamedTextColor.GOLD),
                    Component.text(
                        plugin?.pluginVersion.toString(), NamedTextColor.GREEN
                    )
                ).appendNewline().append(
                    Component.text("[-] Folia: ", NamedTextColor.GOLD),
                    Component.text(ServerVersions.isFolia(), NamedTextColor.GREEN)
                )
            )

            else -> {
                DestinationData.setDestination(executor, destination)
                executor.sendMessage(
                    GameLogger.info(
                        "Destination set to: $destination"
                    )
                )
            }
        }
        return Command.SINGLE_SUCCESS
    }
}
