package de.jawumbo.musicparty.common.bukkit.manager;

import de.jawumbo.musicparty.common.bukkit.game.SingleplayerGame;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameManager {

    private final JavaPlugin javaPlugin;
    private final SongManager songManager;
    private final Map<UUID, SingleplayerGame> singleplayerGames;

    public GameManager(JavaPlugin javaPlugin, SongManager songManager) {
        this.javaPlugin = javaPlugin;
        this.singleplayerGames = new HashMap<>();
        this.songManager = songManager;
    }

    public boolean joinSinglemodeGame(UUID playerUUID) {
        if (isPlaying(playerUUID)) return false;
        if (songManager.getNbsFiles().size() < 4) return false;
        this.singleplayerGames.put(playerUUID, new SingleplayerGame(playerUUID, this.javaPlugin, this.songManager));
        return true;
    }

    public boolean leaveGame(UUID playerUUID) {
        Player player = this.javaPlugin.getServer().getPlayer(playerUUID);
        if (player != null) player.sendExperienceChange(player.getExp(), player.getLevel());

        SingleplayerGame singleplayerGame = this.singleplayerGames.remove(playerUUID);
        if (singleplayerGame == null) return false;
        singleplayerGame.stop();
        return true;
    }

    public boolean isPlaying(UUID player) {
        return this.singleplayerGames.containsKey(player);
    }
}
