package de.jawumbo.musicparty.common.bukkit.runnable;

import de.jawumbo.musicparty.common.bukkit.game.Game;
import de.jawumbo.musicparty.common.bukkit.game.SingleplayerGame;
import de.jawumbo.musicparty.common.bukkit.inventoryholder.SingleplayerResultInventoryHolder;
import de.jawumbo.musicparty.common.bukkit.inventoryholder.SongChooseInventoryHoler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class SongChooseRunnable extends BukkitRunnable {

    private final static int CHOOSE_TIME = 20 * 15;

    private final JavaPlugin javaPlugin;
    private final Game game;

    private int currentTick;
    private SongChooseInventoryHoler songChooseInventoryHoler;

    public SongChooseRunnable(JavaPlugin javaPlugin, Game game) {
        this.javaPlugin = javaPlugin;
        this.game = game;
    }

    @Override
    public void run() {
        int timeLeft = (CHOOSE_TIME - this.currentTick) / 20;
        float progress = (float) (CHOOSE_TIME - this.currentTick) / CHOOSE_TIME;
        switch (this.currentTick++) {
            case 0 -> {
                this.songChooseInventoryHoler = new SongChooseInventoryHoler(this.javaPlugin, this.game);
                this.songChooseInventoryHoler.setTimeLeft(timeLeft);
                Inventory songChooseInventory = this.songChooseInventoryHoler.getInventory();
                for (UUID playerUUID : this.game.getPlayers()) {
                    Player player = this.javaPlugin.getServer().getPlayer(playerUUID);
                    if (player == null) continue;
                    player.openInventory(songChooseInventory);
                }
            }
            case CHOOSE_TIME -> {
                cancel();
                for (UUID playerUUID : this.game.getPlayers()) {
                    Player player = this.javaPlugin.getServer().getPlayer(playerUUID);
                    if (player == null) continue;

                    if (this.game instanceof SingleplayerGame singleplayerGame) {
                        player.openInventory(new SingleplayerResultInventoryHolder(playerUUID, this.javaPlugin, singleplayerGame).getInventory());
                    } // TODO: open multiplayer result inventory
                }
            }
            default -> {
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


}
