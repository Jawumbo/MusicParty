package de.jawumbo.musicparty.common.bukkit.util;

import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@SuppressWarnings("ALL")
@ConfigSerializable
public class ConfigYML {

    private int version;

    private Settings settings = new Settings();
    private Inventories inventories = new Inventories();

    public int version() {
        return version;
    }

    public Settings settings() {
        return settings;
    }

    public Inventories inventories() {
        return inventories;
    }

    @ConfigSerializable
    public static class Settings {
        private int playDurationSeconds = 30;
        private int defaultStartOffsetSeconds = 10;
        private int selectionTimeSeconds = 15;
        private int songOptionsCount = 4;

        public int playDurationSeconds() {
            return playDurationSeconds;
        }

        public int defaultStartOffsetSeconds() {
            return defaultStartOffsetSeconds;
        }

        public int selectionTimeSeconds() {
            return selectionTimeSeconds;
        }

        public int songOptionsCount() {
            return songOptionsCount;
        }
    }

    @ConfigSerializable
    public static class Inventories {
        private SongChooseInventory songChooseInventory = new SongChooseInventory();
        private SingleplayerResultInventory singleplayerResultInventory = new SingleplayerResultInventory();

        public SongChooseInventory songChooseInventory() {
            return songChooseInventory;
        }

        public SingleplayerResultInventory singleplayerResultInventory() {
            return singleplayerResultInventory;
        }
    }

    @ConfigSerializable
    public static class SongChooseInventory {
        private int size = 27;
        private MultiSlotItem songItem = new MultiSlotItem(Material.MUSIC_DISC_5, List.of(10, 12, 14, 16));
        private SimpleItem timeLeftItem = new SimpleItem(Material.CLOCK, 4);
        private SimpleItem fillerItem = new SimpleItem(Material.GRAY_STAINED_GLASS_PANE, -1);

        public int size() {
            return size;
        }

        public MultiSlotItem songItem() {
            return songItem;
        }

        public SimpleItem timeLeftItem() {
            return timeLeftItem;
        }

        public SimpleItem fillerItem() {
            return fillerItem;
        }
    }

    @ConfigSerializable
    public static class SingleplayerResultInventory {
        private int size = 27;
        private ResultGroup guessRight = new ResultGroup(Material.MUSIC_DISC_5, 13, Material.LIME_STAINED_GLASS_PANE);
        private ResultGroup guessWrong = new ResultGroup(Material.MUSIC_DISC_5, 13, Material.RED_STAINED_GLASS_PANE);
        private SimpleItem leaveItem = new SimpleItem(Material.DARK_OAK_DOOR, 10);
        private SimpleItem nextRoundItem = new SimpleItem(Material.FIREWORK_ROCKET, 16);
        private SimpleItem noSongAvailableItem = new SimpleItem(Material.NETHER_STAR, 16);

        public int size() {
            return size;
        }

        public ResultGroup guessRight() {
            return guessRight;
        }

        public ResultGroup guessWrong() {
            return guessWrong;
        }

        public SimpleItem leaveItem() {
            return leaveItem;
        }

        public SimpleItem nextRoundItem() {
            return nextRoundItem;
        }

        public SimpleItem noSongAvailableItem() {
            return noSongAvailableItem;
        }
    }

    @ConfigSerializable
    public static class ResultGroup {
        private SimpleItem songItem;
        private SimpleItem fillerItem;

        public ResultGroup() {
        }

        public ResultGroup(Material songMat, int songSlot, Material fillerMat) {
            this.songItem = new SimpleItem(songMat, songSlot);
            this.fillerItem = new SimpleItem(fillerMat, -1);
        }

        public SimpleItem songItem() {
            return songItem;
        }

        public SimpleItem fillerItem() {
            return fillerItem;
        }
    }

    @ConfigSerializable
    public static class SimpleItem {
        private Material material;
        private int slot;

        public SimpleItem() {
        }

        public SimpleItem(Material material, int slot) {
            this.material = material;
            this.slot = slot;
        }

        public Material material() {
            return material;
        }

        public int slot() {
            return slot;
        }
    }

    @ConfigSerializable
    public static class MultiSlotItem {
        private Material material;
        private List<Integer> slots;

        public MultiSlotItem() {
        }

        public MultiSlotItem(Material material, List<Integer> slots) {
            this.material = material;
            this.slots = slots;
        }

        public Material material() {
            return material;
        }

        public List<Integer> slots() {
            return slots;
        }
    }
}