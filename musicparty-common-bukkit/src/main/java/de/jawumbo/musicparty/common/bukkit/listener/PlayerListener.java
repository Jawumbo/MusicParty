package de.jawumbo.musicparty.common.bukkit.listener;

import de.jawumbo.musicparty.common.bukkit.manager.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerListener implements Listener {

    private final GameManager gameManager;

    public PlayerListener(JavaPlugin javaPlugin, GameManager gameManager) {
        this.gameManager = gameManager;
        javaPlugin.getServer().getPluginManager().registerEvents(this, javaPlugin);
    }

    @EventHandler
    public void handlePlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.gameManager.leaveGame(player.getUniqueId());
    }
}
