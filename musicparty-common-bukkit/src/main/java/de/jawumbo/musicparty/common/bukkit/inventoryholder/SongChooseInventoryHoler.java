package de.jawumbo.musicparty.common.bukkit.inventoryholder;

import de.jawumbo.musicparty.common.bukkit.game.Game;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class SongChooseInventoryHoler implements InventoryHolder {

    public final static NamespacedKey NAMESPACED_KEY = new NamespacedKey(JavaPlugin.getProvidingPlugin(SongChooseInventoryHoler.class), "inventory_choose_song");

    private final Game game;
    private final Inventory inventory;

    public SongChooseInventoryHoler(JavaPlugin javaPlugin, Game game) {
        this.inventory = javaPlugin.getServer().createInventory(this, 27, "Choose song");
        this.game = game;
        NBSFile first = game.getNonPlayedPlaylist().getFirst();
        List<NBSFile> songs = new ArrayList<>();
        songs.add(first);
        fillWithRandomSongs(songs);

        this.inventory.setItem(4, new ItemStack(Material.CLOCK));

        this.inventory.setItem(10, getSongItem(1, songs.get(0)));
        this.inventory.setItem(12, getSongItem(2, songs.get(1)));
        this.inventory.setItem(14, getSongItem(3, songs.get(2)));
        this.inventory.setItem(16, getSongItem(4, songs.get(3)));

        ItemStack placeholderItemStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
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

    private ItemStack getSongItem(int id, NBSFile song) {
        ItemStack itemStack = new ItemStack(Material.MUSIC_DISC_5);

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.getPersistentDataContainer().set(NAMESPACED_KEY, PersistentDataType.STRING, song.songName);
            itemMeta.setItemName("§b§lSong Option #" + id);
            itemMeta.setLore(List.of(
                    "",
                    "§8Song Details:",
                    "§7Title: §f" + song.songName,
                    "§7Artist: §f" + song.originalAuthor,
                    " ",
                    "§6§l» §eClick to lock in this guess!",
                    ""
            ));
            itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    private void fillWithRandomSongs(List<NBSFile> songs) {
        // 1. Filter available songs (O(n))
        List<NBSFile> availableSongs = this.game.getPlaylist().stream()
                .filter(song -> !songs.contains(song))
                .collect(Collectors.toList());

        if (availableSongs.isEmpty()) return;

        // 2. Pick random elements specifically instead of shuffling everything
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int songsToAdd = Math.min(3, availableSongs.size());

        for (int i = 0; i < songsToAdd; i++) {
            // Swap a random element to the current position (Partial Shuffle)
            int randomIndex = random.nextInt(i, availableSongs.size());
            NBSFile randomSong = availableSongs.get(randomIndex);

            // "Draw" element and add to set
            songs.add(randomSong);

            availableSongs.set(randomIndex, availableSongs.get(i));
        }
        Collections.shuffle(songs);
    }

    public void setTimeLeft(int timeLeft) {
        ItemStack timerItemStack = this.inventory.getItem(4);
        if (timerItemStack == null) return;
        timerItemStack = timerItemStack.clone();
        timerItemStack.setAmount(timeLeft);
        ItemMeta timerItemMeta = timerItemStack.getItemMeta();
        if (timerItemMeta != null) {
            timerItemMeta.setDisplayName("§b§lVoting Phase");
            timerItemMeta.setLore(List.of(
                    "",
                    "§7Choose the right song!",
                    "§7Time left: §f" + timeLeft + " s",
                    ""
            ));
            timerItemStack.setItemMeta(timerItemMeta);
        }
        this.inventory.setItem(4, timerItemStack);
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return this.inventory;
    }

    public Game getGame() {
        return game;
    }
}
