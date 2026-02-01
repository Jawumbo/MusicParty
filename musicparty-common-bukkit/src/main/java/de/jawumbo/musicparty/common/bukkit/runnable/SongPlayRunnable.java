package de.jawumbo.musicparty.common.bukkit.runnable;

import de.jawumbo.musicparty.common.bukkit.game.Game;
import de.jawumbo.musicparty.common.bukkit.inventoryholder.SongChooseInventoryHoler;
import de.jawumbo.musicparty.common.bukkit.nbs.NBSPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class SongPlayRunnable extends BukkitRunnable {

    private final static int SONG_PLAY_TIME = 20 * 30;

    private final JavaPlugin javaPlugin;
    private final Game game;
    private BukkitTask nbsPlayerTask;

    private int currentTick;

    public SongPlayRunnable(JavaPlugin javaPlugin, Game game) {
        this.javaPlugin = javaPlugin;
        this.game = game;
    }

    @Override
    public void run() {
        switch (this.currentTick++) {
            case 0 -> {
                this.game.setPunishable(true);
                this.nbsPlayerTask = new NBSPlayer(this.game.getNonPlayedPlaylist().getFirst(), this.game.getPlayers(), this.javaPlugin)
                        .runTaskTimer(this.javaPlugin, 1, 1);
            }
            case SONG_PLAY_TIME -> {
                cancel();
                this.nbsPlayerTask.cancel();
                Inventory inventory = new SongChooseInventoryHoler(this.javaPlugin, this.game).getInventory();
                for (UUID playerUUID : this.game.getPlayers()) {
                    Player player = this.javaPlugin.getServer().getPlayer(playerUUID);
                    if (player == null) continue;
                    player.sendExperienceChange(player.getExp(), player.getLevel());
                    player.openInventory(inventory);
                }
                this.game.setCurrentTask(new SongChooseRunnable(this.javaPlugin, this.game).runTaskTimer(this.javaPlugin, 1, 1));
            }
            default -> {
                float progress = (float) (SONG_PLAY_TIME - currentTick) / SONG_PLAY_TIME;
                for (UUID playerUUID : this.game.getPlayers()) {
                    Player player = this.javaPlugin.getServer().getPlayer(playerUUID);
                    if (player == null) continue;
                    player.sendExperienceChange(progress, (SONG_PLAY_TIME - currentTick) / 20);
                }
            }
        }
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        this.nbsPlayerTask.cancel();
    }
}
