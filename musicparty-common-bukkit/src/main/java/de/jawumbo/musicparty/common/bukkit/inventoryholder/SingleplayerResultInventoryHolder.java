package de.jawumbo.musicparty.common.bukkit.inventoryholder;

import de.jawumbo.musicparty.common.bukkit.game.SingleplayerGame;
import de.jawumbo.musicparty.common.bukkit.manager.ConfigManager;
import de.jawumbo.musicparty.common.bukkit.util.ConfigYML;
import de.jawumbo.musicparty.common.bukkit.util.NBSFile;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SingleplayerResultInventoryHolder implements InventoryHolder {

    public final static NamespacedKey NAMESPACED_KEY = new NamespacedKey(JavaPlugin.getProvidingPlugin(SongChooseInventoryHoler.class), "inventory_singleplayer_result_action");

    private final SingleplayerGame singleplayerGame;
    private final Inventory inventory;

    public SingleplayerResultInventoryHolder(UUID playerUUID, JavaPlugin javaPlugin, ConfigManager configManager, SingleplayerGame singleplayerGame) {
        this.singleplayerGame = singleplayerGame;
        ConfigYML.SingleplayerResultInventory configInventory = configManager.getConfig().inventories().singleplayerResultInventory();

        NBSFile playersChoice = singleplayerGame.getPlayerChoices().get(playerUUID);
        NBSFile rightSong = singleplayerGame.getNonPlayedPlaylist().getFirst();

        String inventoryTitle;
        boolean right = Objects.equals(rightSong, playersChoice);
        if (right)
            inventoryTitle = "§2§lCorrect! §8- §aNice ears!";
        else inventoryTitle = "§4§lWrong... §8- §cTry again!";

        this.inventory = javaPlugin.getServer().createInventory(this, configInventory.size(), inventoryTitle);

        ItemStack leaveItemStack = new ItemStack(configInventory.leaveItem().material());
        ItemMeta leaveItemMeta = leaveItemStack.getItemMeta();
        if (leaveItemMeta != null) {
            leaveItemMeta.getPersistentDataContainer().set(NAMESPACED_KEY, PersistentDataType.STRING, "leave");
            leaveItemMeta.setDisplayName("§c§lQuit Game");
            leaveItemMeta.setLore(List.of(
                    "",
                    "§7Had enough for now?",
                    "§4§l» Click to leave!",
                    ""
            ));
            leaveItemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            leaveItemStack.setItemMeta(leaveItemMeta);
        }
        this.inventory.setItem(configInventory.leaveItem().slot(), leaveItemStack);

        if (singleplayerGame.getNonPlayedPlaylist().size() > 1) {
            ItemStack nextRoundItemStack = new ItemStack(configInventory.nextRoundItem().material());
            ItemMeta nextRoundItemMeta = nextRoundItemStack.getItemMeta();
            if (nextRoundItemMeta != null) {
                nextRoundItemMeta.getPersistentDataContainer().set(NAMESPACED_KEY, PersistentDataType.STRING, "next");
                nextRoundItemMeta.setDisplayName("§a§lNext Round");
                nextRoundItemMeta.setLore(List.of(
                        "",
                        "§7Ready for the next track?",
                        "§e§l» Click to start!",
                        ""
                ));
                nextRoundItemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                nextRoundItemStack.setItemMeta(nextRoundItemMeta);
            }
            this.inventory.setItem(configInventory.nextRoundItem().slot(), nextRoundItemStack);
        } else {
            ItemStack noSongAvailableItemStack = new ItemStack(configInventory.noSongAvailableItem().material());
            ItemMeta noSongAvailableItemMeta = noSongAvailableItemStack.getItemMeta();
            if (noSongAvailableItemMeta != null) {
                noSongAvailableItemMeta.setDisplayName("§e§lThat's all for now!");
                noSongAvailableItemMeta.setLore(List.of(
                        "",
                        "§7You've reached the end of the",
                        "§7current playlist.",
                        "",
                        "§b§oStay tuned! §7We are adding",
                        "§7new tracks in future updates.",
                        ""
                ));
                noSongAvailableItemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                noSongAvailableItemStack.setItemMeta(noSongAvailableItemMeta);
            }
            this.inventory.setItem(configInventory.noSongAvailableItem().slot(), noSongAvailableItemStack);
        }


        if (right) {
            ItemStack rightSongItemStack = new ItemStack(configInventory.guessRight().songItem().material());
            ItemMeta rightSongItemMeta = rightSongItemStack.getItemMeta();
            if (rightSongItemMeta != null) {
                rightSongItemMeta.setDisplayName("§a§l✔ Right Guess!");
                rightSongItemMeta.setLore(List.of(
                        "",
                        "§7You correctly identified:",
                        "§f" + rightSong.songName,
                        "§7by §f" + rightSong.originalAuthor,
                        ""
                ));
                rightSongItemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            }
            rightSongItemStack.setItemMeta(rightSongItemMeta);
            this.inventory.setItem(configInventory.guessRight().songItem().slot(), rightSongItemStack);
        } else {
            ItemStack wronSongItemStack = new ItemStack(configInventory.guessWrong().songItem().material());
            ItemMeta wrongSongItemMeta = wronSongItemStack.getItemMeta();
            if (wrongSongItemMeta != null) {
                wrongSongItemMeta.setDisplayName("§c§l✘ Better luck next time!");
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add("§7The correct song was:");
                lore.add("§f" + rightSong.songName);
                lore.add("§7by §f" + rightSong.originalAuthor);
                if (playersChoice != null) lore.add("§7Your choice: §8" + playersChoice.songName);
                lore.add("");
                wrongSongItemMeta.setLore(lore);
                wrongSongItemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            }
            wronSongItemStack.setItemMeta(wrongSongItemMeta);
            this.inventory.setItem(configInventory.guessWrong().songItem().slot(), wronSongItemStack);
        }


        ItemStack fillerItemStack;
        if (right) fillerItemStack = new ItemStack(configInventory.guessRight().fillerItem().material());
        else fillerItemStack = new ItemStack(configInventory.guessWrong().fillerItem().material());
        ItemMeta fillerItemMeta = fillerItemStack.getItemMeta();
        if (fillerItemMeta != null) {
            fillerItemMeta.setDisplayName(" ");
            fillerItemStack.setItemMeta(fillerItemMeta);
        }
        for (int i = 0; i < this.inventory.getSize(); i++) {
            if (this.inventory.getItem(i) != null) continue;
            this.inventory.setItem(i, fillerItemStack);
        }
    }

    @Override
    public @NonNull Inventory getInventory() {
        return inventory;
    }

    public SingleplayerGame getSingleplayerGame() {
        return singleplayerGame;
    }
}
