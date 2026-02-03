package de.jawumbo.musicparty.common.bukkit;

import de.jawumbo.musicparty.common.bukkit.listener.InventoryListener;
import de.jawumbo.musicparty.common.bukkit.listener.PlayerListener;
import de.jawumbo.musicparty.common.bukkit.manager.ConfigManager;
import de.jawumbo.musicparty.common.bukkit.manager.GameManager;
import de.jawumbo.musicparty.common.bukkit.manager.SongManager;
import de.jawumbo.musicparty.common.bukkit.metrics.BStatsMetrics;
import org.bukkit.plugin.java.JavaPlugin;

public class MusicPartyCommonBukkit {

    private final JavaPlugin javaPlugin;
    private ConfigManager configManager;
    private BStatsMetrics bStatsMetrics;
    private SongManager songManager;
    private GameManager gameManager;

    public MusicPartyCommonBukkit(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    public void onEnable() {
        this.configManager = new ConfigManager(this.javaPlugin);
        this.bStatsMetrics = new BStatsMetrics(this.javaPlugin);
        this.songManager = new SongManager(this.javaPlugin);
        this.gameManager = new GameManager(this.javaPlugin, this.configManager, this.songManager);

        new InventoryListener(this.javaPlugin, this.configManager, this.gameManager);
        new PlayerListener(this.javaPlugin, this.gameManager);
    }

    public void onDisable() {
        this.bStatsMetrics.metrics().shutdown();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public SongManager getSongManager() {
        return this.songManager;
    }

    public GameManager getGameManager() {
        return this.gameManager;
    }
}
