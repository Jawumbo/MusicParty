package de.jawumbo.musicparty.common.bukkit.manager;

import de.jawumbo.musicparty.common.bukkit.helper.NBSParser;
import de.jawumbo.musicparty.common.bukkit.util.NBSFile;
import de.jawumbo.musicparty.common.bukkit.util.Song;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SongManager {

    private final List<NBSFile> nbsFiles;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public SongManager(JavaPlugin javaPlugin) {
        this.nbsFiles = new ArrayList<>();

        File songsFolder = new File(javaPlugin.getDataFolder(), "songs");
        if (!songsFolder.exists()) {
            songsFolder.mkdirs();
            return;
        }

        File[] songFiles = songsFolder.listFiles((dir, name) -> new File(dir, name).isFile() && name.endsWith(".nbs"));
        if (songFiles == null) return;
        for (File file : songFiles) {
            try {
                this.nbsFiles.add(NBSParser.parse(file));
            } catch (IOException e) {
                javaPlugin.getLogger().warning("Could not parse song file " + file.getName());
            }
        }
    }

    private Song getSong(NBSFile nbsFile) {
        Song song = new Song(nbsFile);
        // TODO: load stats from persistent storage
        return song;
    }

    public List<NBSFile> getPlaylist() {
        List<NBSFile> playlist = new ArrayList<>(nbsFiles);
        Collections.shuffle(playlist);
        return playlist;
    }

    public List<NBSFile> getNbsFiles() {
        return nbsFiles;
    }
}
