package de.jawumbo.musicparty.common.bukkit.inventoryholder;

import de.jawumbo.musicparty.common.bukkit.game.Game;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class SongChooseInventoryHoler implements InventoryHolder {

    public final static NamespacedKey NAMESPACED_KEY = new NamespacedKey(JavaPlugin.getProvidingPlugin(SongChooseInventoryHoler.class), "inventory_choose_song");

    private final ConfigYML.SongChooseInventory configInventory;
    private final Game game;
    private final Inventory inventory;

    public SongChooseInventoryHoler(JavaPlugin javaPlugin, ConfigManager configManager, Game game) {
        this.configInventory = configManager.getConfig().inventories().songChooseInventory();
        this.inventory = javaPlugin.getServer().createInventory(this, this.configInventory.size(), "Choose song");
        this.game = game;
        NBSFile first = game.getNonPlayedPlaylist().getFirst();
        List<NBSFile> songs = new ArrayList<>();
        songs.add(first);
        int songOptionsCount = configManager.getConfig().settings().songOptionsCount();
        fillWithRandomSongs(songs, songOptionsCount);

        this.inventory.setItem(this.configInventory.timeLeftItem().slot(), new ItemStack(this.configInventory.timeLeftItem().material()));

        for (int i = 0; i < songOptionsCount; i++)
            this.inventory.setItem(this.configInventory.songItem().slots().get(i), getSongItem(i + 1, songs.get(i)));

        ItemStack fillerItemStack = new ItemStack(this.configInventory.fillerItem().material());
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

    private ItemStack getSongItem(int id, NBSFile song) {
        ItemStack itemStack = new ItemStack(this.configInventory.songItem().material());

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

    private void fillWithRandomSongs(List<NBSFile> songs, int amount) {
        // 1. Filter available songs (O(n))
        List<NBSFile> availableSongs = this.game.getPlaylist().stream()
                .filter(song -> !songs.contains(song))
                .collect(Collectors.toList());

        if (availableSongs.isEmpty()) return;

        // 2. Pick random elements specifically instead of shuffling everything
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int songsToAdd = Math.min(amount, availableSongs.size());

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
        ItemStack timerItemStack = this.inventory.getItem(this.configInventory.timeLeftItem().slot());
        if (timerItemStack == null) return;
        //timerItemStack = timerItemStack.clone();
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
        //this.inventory.setItem(this.configInventory.timeLeftItem().slot(), timerItemStack);
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return this.inventory;
    }

    public Game getGame() {
        return this.game;
    }
}
