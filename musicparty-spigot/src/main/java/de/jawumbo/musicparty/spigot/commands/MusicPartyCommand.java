package de.jawumbo.musicparty.spigot.commands;

import de.jawumbo.musicparty.common.bukkit.manager.ConfigManager;
import de.jawumbo.musicparty.common.bukkit.manager.GameManager;
import de.jawumbo.musicparty.common.bukkit.manager.SongManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.stream.Stream;

/*
 * /musicparty - Zeigt Infos über das Plugin an
 * /musicparty help - Zeigt alle Befehle an
 * /musicparty join - Betritt den Singleplayer Modus
 * /musicparty leave - Verlässt den Singleplayer Modus
 */
public class MusicPartyCommand implements TabExecutor {

    private final ConfigManager configManager;
    private final GameManager gameManager;
    private final SongManager songManager;

    public MusicPartyCommand(JavaPlugin javaPlugin, ConfigManager configManager, GameManager gameManager, SongManager songManager) {
        this.configManager = configManager;
        this.gameManager = gameManager;
        this.songManager = songManager;

        PluginCommand pluginCommand = javaPlugin.getCommand("musicparty");
        if (pluginCommand == null) return;
        pluginCommand.setExecutor(this);
        pluginCommand.setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("""
                    §8[§bMusicParty§8] §7An interactive musical quiz game by Jawumbo!
                    §8- §7Use §e/musicparty help §7for commands.
                    """);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help":
                sender.sendMessage("""
                        §8[§bMusicParty§8] §7Commands:
                        §8- §e/musicparty §8- §7Show plugin information
                        §8- §e/musicparty help §8- §7Show all commands"
                        §8- §e/musicparty join §8- §7Join singleplayer mode"
                        §8- §e/musicparty leave §8- §7Leave singleplayer mode
                        """);
                return true;

            case "join":
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§8[§bMusicParty§8] §cOnly players can join the game!");
                    return true;
                }
                if (this.gameManager.isPlaying(player.getUniqueId())) {
                    sender.sendMessage("§8[§bMusicParty§8] §cYou are already playing!");
                    return true;
                }
                if (this.songManager.getNbsFiles().size() < this.configManager.getConfig().settings().songOptionsCount()) {
                    sender.sendMessage("§8[§bMusicParty§8] §cNot enough songs available! At least 4 songs are required.");
                    return true;
                }
                if (this.gameManager.joinSinglemodeGame(player.getUniqueId()))
                    sender.sendMessage("§8[§bMusicParty§8] §aYou joined the game!");
                else sender.sendMessage("§8[§bMusicParty§8] §cCould not join the game!");
                return true;

            case "leave":
                if (!(sender instanceof Player leavePlayer)) {
                    sender.sendMessage("§8[§bMusicParty§8] §cOnly players can leave the game!");
                    return true;
                }
                if (this.gameManager.leaveGame(leavePlayer.getUniqueId()))
                    sender.sendMessage("§8[§bMusicParty§8] §aYou left the game!");
                else sender.sendMessage("§8[§bMusicParty§8] §cYou are not in a game!");
                return true;

            default:
                sender.sendMessage("§8[§bMusicParty§8] §cUnknown command! Use §e/musicparty help §cfor help.");
                return true;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if (args.length == 1)
            return Stream.of("help", "join", "leave")
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        return List.of();
    }
}
