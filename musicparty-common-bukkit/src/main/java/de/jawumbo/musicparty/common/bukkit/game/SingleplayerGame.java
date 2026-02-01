package de.jawumbo.musicparty.common.bukkit.game;

import de.jawumbo.musicparty.common.bukkit.manager.SongManager;
import de.jawumbo.musicparty.common.bukkit.runnable.SongPlayRunnable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class SingleplayerGame extends Game {

    public SingleplayerGame(UUID playerUUID, JavaPlugin javaPlugin, SongManager songManager) {
        super(javaPlugin, songManager.getPlaylist());
        getPlayers().add(playerUUID);
        start();
    }

    public void restart() {
        setPunishable(false);
        getPlayerChoices().clear();
        setCurrentTask(null);
        getNonPlayedPlaylist().removeFirst();
        start();
    }

    private void start() {
        if (getPlaylist().size() < 4) return;
        if (getNonPlayedPlaylist().isEmpty()) return;
        this.currentTask = new SongPlayRunnable(this.javaPlugin, this).runTaskTimer(this.javaPlugin, 1, 1);
    }

    public void stop() {
        // TODO: punish player for ragequit :)
        if (this.currentTask != null) this.currentTask.cancel();
        getPlayers().clear();
    }

}
