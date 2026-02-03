package de.jawumbo.musicparty.common.bukkit.helper;

import de.jawumbo.musicparty.common.bukkit.util.NBSFile;
import de.jawumbo.musicparty.common.bukkit.util.NBSInstrument;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;

public class NBSParser {
    public static NBSFile parse(File file) throws IOException {
        byte[] data = Files.readAllBytes(file.toPath());
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        NBSFile nbs = new NBSFile();

        short firstValue = buffer.getShort();

        if (firstValue == 0) {
            // version 1-5
            nbs.nbsVersion = buffer.get();
            nbs.vanillaInstrumentCount = buffer.get();
            nbs.songLength = buffer.getShort();
        } else {
            // version 0
            nbs.nbsVersion = 0;
            nbs.songLength = firstValue;
        }

        nbs.layerCount = buffer.getShort();
        nbs.songName = readString(buffer);
        nbs.songAuthor = readString(buffer);
        nbs.originalAuthor = readString(buffer);
        nbs.description = readString(buffer);
        nbs.tempo = buffer.getShort();

        nbs.autoSave = buffer.get() == 1;
        nbs.autoSaveDuration = buffer.get();
        nbs.timeSignature = buffer.get();
        nbs.minutesSpent = buffer.getInt();
        nbs.leftClicks = buffer.getInt();
        nbs.rightClicks = buffer.getInt();
        nbs.noteBlocksAdded = buffer.getInt();
        nbs.noteBlocksRemoved = buffer.getInt();
        nbs.importFileName = readString(buffer);

        // version 4: looping
        if (nbs.nbsVersion >= 4) {
            nbs.looping = buffer.get() == 1;
            nbs.loopMaxCount = buffer.get();
            nbs.loopStartTick = buffer.getShort();
        }

        readContent(buffer, nbs);
        calculateMinecraftData(nbs);
        return nbs;
    }

    public static void readContent(ByteBuffer buffer, NBSFile nbs) {
        // 1. parse notes
        short currentTick = -1;
        while (buffer.remaining() >= 2) {
            short jumpTicks = buffer.getShort();
            if (jumpTicks == 0) break; // end of the note section
            currentTick += jumpTicks;

            short currentLayer = -1;
            while (true) {
                short jumpLayers = buffer.getShort();
                if (jumpLayers == 0) break; // end of this tick
                currentLayer += jumpLayers;

                NBSFile.Note note = new NBSFile.Note();
                note.tick = currentTick;
                note.layer = currentLayer;
                note.instrument = buffer.get();
                note.key = buffer.get();

                if (nbs.nbsVersion >= 4) {
                    note.velocity = buffer.get();
                    note.panning = buffer.get();
                    note.pitch = buffer.getShort();
                }

                nbs.addNoteToIndex(note);
                nbs.notes.add(note);
            }
        }

        // 2. layer informations
        for (int i = 0; i < nbs.layerCount; i++) {
            NBSFile.Layer layer = new NBSFile.Layer();
            layer.name = readString(buffer);
            if (nbs.nbsVersion >= 4) {
                layer.lock = buffer.get();
            }
            layer.volume = buffer.get();
            if (nbs.nbsVersion >= 2) {
                layer.panning = buffer.get();
            }
            nbs.layers.add(layer);
        }

        // 3. custom instruments
        if (buffer.hasRemaining()) {
            byte customInstrCount = buffer.get();
            for (int i = 0; i < customInstrCount; i++) {
                if (!buffer.hasRemaining()) break;

                NBSFile.CustomInstrument inst = new NBSFile.CustomInstrument();
                inst.name = readString(buffer);
                inst.soundFile = readString(buffer);

                if (buffer.hasRemaining()) inst.pitch = buffer.get();
                if (buffer.hasRemaining()) inst.pressKey = buffer.get() == 1;

                nbs.customInstruments.add(inst);
            }
        }
    }

    private static void calculateMinecraftData(NBSFile nbs) {
        for (NBSFile.Note note : nbs.notes) {
            // volume
            float vol = (note.velocity / 100f);
            if (note.layer < nbs.layers.size()) {
                vol *= (nbs.layers.get(note.layer).volume / 100f);
            }
            note.calculatedVolume = vol;

            // pitch
            float instOffset = 0;
            if (note.instrument >= 16) {
                int customIdx = note.instrument - 16;
                if (customIdx < nbs.customInstruments.size()) {
                    instOffset = nbs.customInstruments.get(customIdx).pitch - 45;
                }
            }
            // formula: 2^((Key - 45 + Offset + Cent/100) / 12)
            note.calculatedPitch = (float) Math.pow(2, (note.key - 45 + instOffset + (note.pitch / 100.0)) / 12.0);

            // prepare song
            if (note.instrument < 16) {
                note.calculatedSound = NBSInstrument.getSoundName(note.instrument);
            } else {
                int customIdx = note.instrument - 16;
                if (customIdx < nbs.customInstruments.size()) {
                    NBSFile.CustomInstrument custom = nbs.customInstruments.get(customIdx);
                    String soundPath = custom.soundFile.split("\\.")[0];
                    note.calculatedSound = soundPath.isEmpty() ? custom.name.toLowerCase() : soundPath;
                } else {
                    note.calculatedSound = NBSInstrument.getSoundName(0); // fallback Piano
                }
            }
        }
    }

    private static String readString(ByteBuffer buffer) {
        if (buffer.remaining() < 4) return "";
        int length = buffer.getInt();
        if (length <= 0 || length > buffer.remaining()) return "";

        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return new String(bytes);
    }
}
