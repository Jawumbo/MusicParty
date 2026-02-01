package de.jawumbo.musicparty.common.bukkit.listener;

import de.jawumbo.musicparty.common.bukkit.game.Game;
import de.jawumbo.musicparty.common.bukkit.game.SingleplayerGame;
import de.jawumbo.musicparty.common.bukkit.inventoryholder.SingleplayerResultInventoryHolder;
import de.jawumbo.musicparty.common.bukkit.inventoryholder.SongChooseInventoryHoler;
import de.jawumbo.musicparty.common.bukkit.manager.GameManager;
import de.jawumbo.musicparty.common.bukkit.util.NBSFile;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class InventoryListener implements Listener {

    private final JavaPlugin javaPlugin;
    private final GameManager gameManager;

    public InventoryListener(JavaPlugin javaPlugin, GameManager gameManager) {
        this.javaPlugin = javaPlugin;
        this.gameManager = gameManager;
        javaPlugin.getServer().getPluginManager().registerEvents(this, javaPlugin);
    }

    @EventHandler
    public void handleChooseInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;
        if (!(inventory.getHolder() instanceof SongChooseInventoryHoler holder)) return;

        event.setCancelled(true);

        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null) return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        String chosedFileName = itemMeta.getPersistentDataContainer().get(SongChooseInventoryHoler.NAMESPACED_KEY, PersistentDataType.STRING);
        NBSFile chosedSong = null;
        Game game = holder.getGame();
        for (NBSFile nbsFile : game.getPlaylist()) {
            if (Objects.equals(nbsFile.songName, chosedFileName)) {
                chosedSong = nbsFile;
                break;
            }
        }
        if (chosedSong == null) return;

        HumanEntity humanEntity = event.getWhoClicked();
        game.getPlayerChoices().put(humanEntity.getUniqueId(), chosedSong);
        event.getView().close();

        if (game instanceof SingleplayerGame singleplayerGame) {
            game.getCurrentTask().cancel();
            humanEntity.openInventory(new SingleplayerResultInventoryHolder(humanEntity.getUniqueId(), this.javaPlugin, singleplayerGame).getInventory());
        }
    }

    @EventHandler
    public void handleSinglePlayerResultInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;
        if (!(inventory.getHolder() instanceof SingleplayerResultInventoryHolder holder)) return;

        event.setCancelled(true);

        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null) return;

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        String action = itemMeta.getPersistentDataContainer().get(SingleplayerResultInventoryHolder.NAMESPACED_KEY, PersistentDataType.STRING);

        switch (action) {
            case "next" -> holder.getSingleplayerGame().restart();
            case "leave" -> this.gameManager.leaveGame(event.getWhoClicked().getUniqueId());
            case null, default -> {
                return;
            }
        }

        event.getView().close();
    }

    @EventHandler
    public void handleSingleplayerResultInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder() instanceof SingleplayerResultInventoryHolder holder)) return;

        HumanEntity humanEntity = event.getPlayer();
        if (!this.gameManager.isPlaying(humanEntity.getUniqueId())) return;

        if (!holder.getSingleplayerGame().getCurrentTask().isCancelled()) return;

        this.gameManager.leaveGame(humanEntity.getUniqueId());
    }

}
