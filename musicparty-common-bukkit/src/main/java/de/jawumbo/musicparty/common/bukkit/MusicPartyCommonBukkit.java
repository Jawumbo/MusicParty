package de.jawumbo.musicparty.common.bukkit;

import de.jawumbo.musicparty.common.bukkit.listener.InventoryListener;
import de.jawumbo.musicparty.common.bukkit.listener.PlayerListener;
import de.jawumbo.musicparty.common.bukkit.manager.GameManager;
import de.jawumbo.musicparty.common.bukkit.manager.SongManager;
import de.jawumbo.musicparty.common.bukkit.metrics.BStatsMetrics;
import org.bukkit.plugin.java.JavaPlugin;

public class MusicPartyCommonBukkit {

    private final JavaPlugin javaPlugin;
    private BStatsMetrics bStatsMetrics;
    private SongManager songManager;
    private GameManager gameManager;

    public MusicPartyCommonBukkit(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    public void onEnable() {
        this.bStatsMetrics = new BStatsMetrics(this.javaPlugin);

        this.songManager = new SongManager(this.javaPlugin);
        this.gameManager = new GameManager(javaPlugin, songManager);

        new InventoryListener(this.javaPlugin, this.gameManager);
        new PlayerListener(this.javaPlugin, this.gameManager);
    }

    public void onDisable() {
        this.bStatsMetrics.metrics().shutdown();
    }

    public SongManager getSongManager() {
        return songManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
