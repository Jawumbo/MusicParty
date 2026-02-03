package de.jawumbo.musicparty.common.bukkit.runnable;

import de.jawumbo.musicparty.common.bukkit.game.Game;
import de.jawumbo.musicparty.common.bukkit.game.SingleplayerGame;
import de.jawumbo.musicparty.common.bukkit.inventoryholder.SingleplayerResultInventoryHolder;
import de.jawumbo.musicparty.common.bukkit.inventoryholder.SongChooseInventoryHoler;
import de.jawumbo.musicparty.common.bukkit.manager.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class SongChooseRunnable extends BukkitRunnable {

    private final JavaPlugin javaPlugin;
    private final ConfigManager configManager;
    private final Game game;

    private final int chooseTime;
    private int currentTick;
    private SongChooseInventoryHoler songChooseInventoryHoler;

    public SongChooseRunnable(JavaPlugin javaPlugin, ConfigManager configManager, Game game) {
        this.javaPlugin = javaPlugin;
        this.chooseTime = 20 * configManager.getConfig().settings().selectionTimeSeconds();
        this.configManager = configManager;
        this.game = game;
    }

    @Override
    public void run() {
        int timeLeft = (chooseTime - this.currentTick) / 20;
        float progress = (float) (chooseTime - this.currentTick) / chooseTime;
        int i = this.currentTick++;
        if (i == 0) {
            this.songChooseInventoryHoler = new SongChooseInventoryHoler(this.javaPlugin, this.configManager, this.game);
            this.songChooseInventoryHoler.setTimeLeft(timeLeft);
            Inventory songChooseInventory = this.songChooseInventoryHoler.getInventory();
            for (UUID playerUUID : this.game.getPlayers()) {
                Player player = this.javaPlugin.getServer().getPlayer(playerUUID);
                if (player == null) continue;
                player.openInventory(songChooseInventory);
            }
        } else if (i == chooseTime) {
            cancel();
            for (UUID playerUUID : this.game.getPlayers()) {
                Player player = this.javaPlugin.getServer().getPlayer(playerUUID);
                if (player == null) continue;

                if (this.game instanceof SingleplayerGame singleplayerGame) {
                    player.openInventory(new SingleplayerResultInventoryHolder(playerUUID, this.javaPlugin, this.configManager, singleplayerGame).getInventory());
                } // TODO: open multiplayer result inventory
            }
        } else {
            if (this.songChooseInventoryHoler != null) this.songChooseInventoryHoler.setTimeLeft(timeLeft);
            for (UUID playerUUID : this.game.getPlayers()) {
                Player player = this.javaPlugin.getServer().getPlayer(playerUUID);
                if (player == null) continue;
                player.sendExperienceChange(progress, timeLeft);


                if (player.getOpenInventory().getTopInventory() != this.songChooseInventoryHoler.getInventory())
                    if (!this.game.getPlayerChoices().containsKey(playerUUID))
                        player.openInventory(this.songChooseInventoryHoler.getInventory());
            }
        }
    }


}
