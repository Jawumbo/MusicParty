package de.jawumbo.musicparty.paper.commands;

import de.jawumbo.musicparty.common.bukkit.manager.ConfigManager;
import de.jawumbo.musicparty.common.bukkit.manager.GameManager;
import de.jawumbo.musicparty.common.bukkit.manager.SongManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class MusicPartyCommand {

    private final ConfigManager configManager;
    private final GameManager gameManager;
    private final SongManager songManager;

    private final Component prefix = text()
            .append(text("[", NamedTextColor.DARK_GRAY))
            .append(text("MusicParty", NamedTextColor.AQUA))
            .append(text("] ", NamedTextColor.DARK_GRAY))
            .build();

    public MusicPartyCommand(JavaPlugin plugin, ConfigManager configManager, GameManager gameManager, SongManager songManager) {
        this.configManager = configManager;
        this.gameManager = gameManager;
        this.songManager = songManager;

        LifecycleEventManager<Plugin> manager = plugin.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            // main command: /musicparty
            commands.register(
                    Commands.literal("musicparty")
                            .requires(source -> source.getSender().hasPermission("musicparty.use"))

                            .executes(ctx -> {
                                info(ctx.getSource());
                                return 1;
                            })
                            // subcommand: help
                            .then(Commands.literal("help")
                                    .executes(ctx -> {
                                        help(ctx.getSource());
                                        return 1;
                                    })
                            )
                            // subcommand: join
                            .then(Commands.literal("join")
                                    .requires(source -> source.getSender() instanceof Player)
                                    .executes(ctx -> {
                                        join(ctx.getSource());
                                        return 1;
                                    })
                            )
                            // subcommand: leave
                            .then(Commands.literal("leave")
                                    .requires(source -> source.getSender() instanceof Player)
                                    .executes(ctx -> {
                                        leave(ctx.getSource());
                                        return 1;
                                    })
                            )
                            .build(),
                    "Music quiz plugin main command",
                    List.of("mp")
            );
        });
    }

    private void info(CommandSourceStack source) {
        source.getSender().sendMessage(text()
                .append(prefix)
                .append(text("An interactive musical quiz game by Jawumbo!", NamedTextColor.GRAY)).appendNewline()
                .append(text("- ", NamedTextColor.DARK_GRAY))
                .append(text("Use ", NamedTextColor.GRAY))
                .append(text("/musicparty help ", NamedTextColor.YELLOW))
                .append(text("for commands.", NamedTextColor.GRAY))
        );
    }

    private void help(CommandSourceStack source) {
        source.getSender().sendMessage(text()
                .append(prefix)
                .append(text("Commands:", NamedTextColor.GRAY)).appendNewline()
                .append(createHelpLine("/musicparty", "Show plugin information")).appendNewline()
                .append(createHelpLine("/musicparty help", "Show all commands")).appendNewline()
                .append(createHelpLine("/musicparty join", "Join singleplayer mode")).appendNewline()
                .append(createHelpLine("/musicparty leave", "Leave singleplayer mode"))
        );
    }

    // Hilfsmethode für die Hilfe-Zeilen
    private Component createHelpLine(String command, String description) {
        return text()
                .append(text("- ", NamedTextColor.DARK_GRAY))
                .append(text(command, NamedTextColor.YELLOW))
                .append(text(" - ", NamedTextColor.DARK_GRAY))
                .append(text(description, NamedTextColor.GRAY))
                .build();
    }

    private void join(CommandSourceStack source) {
        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(prefix.append(text("Only players can join the game!", NamedTextColor.RED)));
            return;
        }

        if (this.gameManager.isPlaying(player.getUniqueId())) {
            player.sendMessage(prefix.append(text("You are already playing!", NamedTextColor.RED)));
            return;
        }

        if (this.songManager.getNbsFiles().size() < this.configManager.getConfig().settings().songOptionsCount()) {
            player.sendMessage(prefix.append(text("Not enough songs available! At least 4 songs are required.", NamedTextColor.RED)));
            return;
        }

        if (this.gameManager.joinSinglemodeGame(player.getUniqueId()))
            player.sendMessage(prefix.append(text("You joined the game!", NamedTextColor.GREEN)));
        else
            player.sendMessage(prefix.append(text("Could not join the game!", NamedTextColor.RED)));
    }

    private void leave(CommandSourceStack source) {
        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(prefix.append(text("Only players can leave the game!", NamedTextColor.RED)));
            return;
        }

        if (this.gameManager.leaveGame(player.getUniqueId()))
            player.sendMessage(prefix.append(text("You left the game!", NamedTextColor.GREEN)));
        else
            player.sendMessage(prefix.append(text("You are not in a game!", NamedTextColor.RED)));
    }
}