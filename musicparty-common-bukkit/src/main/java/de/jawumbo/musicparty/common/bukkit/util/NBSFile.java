package de.jawumbo.musicparty.common.bukkit.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NBSFile {
    // index system
    private final Map<Integer, List<Note>> noteIndex = new HashMap<>();

    // header data
    public byte nbsVersion;
    public byte vanillaInstrumentCount;
    public short songLength;
    public short layerCount;
    public String songName;
    public String songAuthor;
    public String originalAuthor;
    public String description;
    public short tempo;
    public boolean autoSave;
    public byte autoSaveDuration;
    public byte timeSignature;
    public int minutesSpent;
    public int leftClicks, rightClicks;
    public int noteBlocksAdded, noteBlocksRemoved;
    public String importFileName;
    public boolean looping;
    public byte loopMaxCount;
    public short loopStartTick;

    // song data
    public List<Note> notes = new ArrayList<>();
    public List<Layer> layers = new ArrayList<>();
    public List<CustomInstrument> customInstruments = new ArrayList<>();

    public void addNoteToIndex(Note note) {
        noteIndex.computeIfAbsent((int) note.tick, k -> new ArrayList<>()).add(note);
    }

    public List<Note> getNotesAtTick(int tick) {
        return noteIndex.getOrDefault(tick, new ArrayList<>());
    }

    public static class Note {
        public short tick;
        public short layer;
        public byte instrument; // 0-15 sind Vanilla, ab 16 Custom
        public byte key;        // 0-88 (Klaviertasten), 45 ist Fis4

        // version 4
        public byte velocity = 100;
        public byte panning = 100; // 0 = rechts, 100 = mitte, 200 = links
        public short pitch = 0;    // Detune in Cent

        // minecraft data
        public float calculatedVolume;
        public float calculatedPitch;
        public String calculatedSound;
    }

    public static class Layer {
        public String name = "";
        public byte lock = 0;
        public byte volume = 100;
        public byte panning = 100;
    }

    public static class CustomInstrument {
        public String name;
        public String soundFile;
        public byte pitch;
        public boolean pressKey;
    }
}
