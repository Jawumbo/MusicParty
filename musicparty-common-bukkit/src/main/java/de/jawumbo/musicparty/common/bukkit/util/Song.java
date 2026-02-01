package de.jawumbo.musicparty.common.bukkit.util;

public class Song {

    private final NBSFile nbsFile;
    private int playCount, rightCount, wrongCount;

    public Song(NBSFile nbsFile) {
        this.nbsFile = nbsFile;
    }

    public NBSFile getNbsFile() {
        return nbsFile;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void incrementPlayCount() {
        this.playCount++;
    }

    public int getRightCount() {
        return rightCount;
    }

    public void incrementRightCount() {
        this.rightCount++;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public void incrementWrongCount() {
        this.wrongCount++;
    }
}
