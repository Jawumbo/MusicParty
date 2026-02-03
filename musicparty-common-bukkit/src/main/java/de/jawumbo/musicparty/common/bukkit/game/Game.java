package de.jawumbo.musicparty.common.bukkit.game;

import de.jawumbo.musicparty.common.bukkit.manager.ConfigManager;
import de.jawumbo.musicparty.common.bukkit.util.NBSFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public abstract class Game {

    final JavaPlugin javaPlugin;
    final ConfigManager configManager;
    private final List<NBSFile> playlist;
    private final List<NBSFile> nonPlayedPlaylist;
    private final List<UUID> players;
    private final Map<UUID, NBSFile> playerChoices;
    BukkitTask currentTask;
    private boolean punishable;

    protected Game(JavaPlugin javaPlugin, ConfigManager configManager, List<NBSFile> playlist) {
        this.javaPlugin = javaPlugin;
        this.configManager = configManager;
        this.playlist = playlist;
        this.nonPlayedPlaylist = new ArrayList<>(playlist);
        this.players = new ArrayList<>();
        this.playerChoices = new HashMap<>();
    }

    public List<NBSFile> getPlaylist() {
        return playlist;
    }

    public List<NBSFile> getNonPlayedPlaylist() {
        return nonPlayedPlaylist;
    }

    public BukkitTask getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(BukkitTask currentTask) {
        this.currentTask = currentTask;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public Map<UUID, NBSFile> getPlayerChoices() {
        return playerChoices;
    }

    public boolean isPunishable() {
        return punishable;
    }

    public void setPunishable(boolean punishable) {
        this.punishable = punishable;
    }
}
