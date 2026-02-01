package de.jawumbo.musicparty.common.bukkit.inventoryholder;

import de.jawumbo.musicparty.common.bukkit.game.SingleplayerGame;
import de.jawumbo.musicparty.common.bukkit.util.NBSFile;
import org.bukkit.Material;
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

    public SingleplayerResultInventoryHolder(UUID playerUUID, JavaPlugin javaPlugin, SingleplayerGame singleplayerGame) {
        this.singleplayerGame = singleplayerGame;

        NBSFile playersChoice = singleplayerGame.getPlayerChoices().get(playerUUID);
        NBSFile rightSong = singleplayerGame.getNonPlayedPlaylist().getFirst();

        String inventoryTitle;
        boolean right = Objects.equals(rightSong, playersChoice);
        if (right)
            inventoryTitle = "§2§lCorrect! §8- §aNice ears!";
        else inventoryTitle = "§4§lWrong... §8- §cTry again!";

        this.inventory = javaPlugin.getServer().createInventory(this, 27, inventoryTitle);

        ItemStack leaveItemStack = new ItemStack(Material.DARK_OAK_DOOR);
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
        this.inventory.setItem(10, leaveItemStack);

        if (singleplayerGame.getNonPlayedPlaylist().size() > 1) {
            ItemStack restartItemStack = new ItemStack(Material.FIREWORK_ROCKET);
            ItemMeta restartItemMeta = restartItemStack.getItemMeta();
            if (restartItemMeta != null) {
                restartItemMeta.getPersistentDataContainer().set(NAMESPACED_KEY, PersistentDataType.STRING, "next");
                restartItemMeta.setDisplayName("§a§lNext Round");
                restartItemMeta.setLore(List.of(
                        "",
                        "§7Ready for the next track?",
                        "§e§l» Click to start!",
                        ""
                ));
                restartItemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                restartItemStack.setItemMeta(restartItemMeta);
            }
            this.inventory.setItem(16, restartItemStack);
        } else {
            ItemStack commingSoonItemStack = new ItemStack(Material.NETHER_STAR);
            ItemMeta commingSoonItemMeta = commingSoonItemStack.getItemMeta();
            if (commingSoonItemMeta != null) {
                commingSoonItemMeta.setDisplayName("§e§lThat's all for now!");
                commingSoonItemMeta.setLore(List.of(
                        "",
                        "§7You've reached the end of the",
                        "§7current playlist.",
                        "",
                        "§b§oStay tuned! §7We are adding",
                        "§7new tracks in future updates.",
                        ""
                ));
                commingSoonItemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                commingSoonItemStack.setItemMeta(commingSoonItemMeta);
            }
            this.inventory.setItem(16, commingSoonItemStack);
        }

        ItemStack songItemStack = new ItemStack(Material.MUSIC_DISC_5);
        ItemMeta songItemMeta = songItemStack.getItemMeta();
        if (songItemMeta != null) {
            if (right) {
                songItemMeta.setDisplayName("§a§l✔ Right Guess!");
                songItemMeta.setLore(List.of(
                        "",
                        "§7You correctly identified:",
                        "§f" + rightSong.songName,
                        "§7by §f" + rightSong.originalAuthor,
                        ""
                ));
            } else {
                songItemMeta.setDisplayName("§c§l✘ Better luck next time!");
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add("§7The correct song was:");
                lore.add("§f" + rightSong.songName);
                lore.add("§7by §f" + rightSong.originalAuthor);
                if (playersChoice != null) lore.add("§7Your choice: §8" + playersChoice.songName);
                lore.add("");
                songItemMeta.setLore(lore);
            }
            songItemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            songItemStack.setItemMeta(songItemMeta);
        }
        this.inventory.setItem(13, songItemStack);


        ItemStack placeholderItemStack;
        if (right) placeholderItemStack = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        else placeholderItemStack = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta placeholderItemMeta = placeholderItemStack.getItemMeta();
        if (placeholderItemMeta != null) {
            placeholderItemMeta.setDisplayName(" ");
            placeholderItemStack.setItemMeta(placeholderItemMeta);
        }
        for (int i = 0; i < this.inventory.getSize(); i++) {
            if (this.inventory.getItem(i) != null) continue;
            this.inventory.setItem(i, placeholderItemStack);
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
