package de.jawumbo.musicparty.common.bukkit.nbs;

import de.jawumbo.musicparty.common.bukkit.manager.ConfigManager;
import de.jawumbo.musicparty.common.bukkit.util.NBSFile;
import de.jawumbo.musicparty.common.bukkit.util.NBSLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class NBSPlayer extends BukkitRunnable {

    private final JavaPlugin javaPlugin;
    private final NBSFile nbs;
    private final List<UUID> audience;

    private double nbsTickCounter;
    private int lastPlayedTick;
    private int loopsDone;

    public NBSPlayer(NBSFile nbs, List<UUID> audience, JavaPlugin javaPlugin, ConfigManager configManager) {
        this.javaPlugin = javaPlugin;

        int startOffsetSeconds = configManager.getConfig().settings().defaultStartOffsetSeconds();
        double startTickDouble = startOffsetSeconds * (nbs.tempo / 100.0);
        int startTick = (int) Math.floor(startTickDouble);

        if (startTick < 0) startTick = 0;
        if (startTick > nbs.songLength) startTick = nbs.songLength;

        this.nbsTickCounter = startTick;
        this.lastPlayedTick = startTick - 1;

        this.nbs = nbs;
        this.audience = audience;
    }

    @Override
    public void run() {
        if (this.audience.isEmpty()) {
            cancel();
            return;
        }

        double ticksPerServerTick = (this.nbs.tempo / 100.0) / 20.0;
        this.nbsTickCounter += ticksPerServerTick;
        int currentNBSTick = (int) Math.floor(this.nbsTickCounter);

        if (currentNBSTick != this.lastPlayedTick) {
            for (int t = this.lastPlayedTick + 1; t <= currentNBSTick; t++) {
                if (t > this.nbs.songLength) {
                    if (this.nbs.looping && (this.nbs.loopMaxCount == 0 || this.loopsDone < this.nbs.loopMaxCount)) {
                        this.loopsDone++;
                        this.nbsTickCounter = this.nbs.loopStartTick;
                        this.lastPlayedTick = this.nbs.loopStartTick - 1;
                        return;
                    }
                    cancel();
                    return;
                }

                for (NBSFile.Note note : nbs.getNotesAtTick(t))
                    playNote(note);
            }
            this.lastPlayedTick = currentNBSTick;
        }
    }

    private void playNote(NBSFile.Note note) {
        for (UUID uuid : this.audience) {
            Player player = this.javaPlugin.getServer().getPlayer(uuid);
            if (player == null) continue;

            Location playerLocation = player.getLocation().clone();
            NBSLocation nbsLocation = new NBSLocation(playerLocation.getX(), playerLocation.getZ(), playerLocation.getYaw());
            NBSLocation.calculateNoteLocation(nbsLocation, note);
            Location location = new Location(player.getWorld(), nbsLocation.x, playerLocation.getY(), nbsLocation.z);
            player.playSound(location, note.calculatedSound, note.calculatedVolume, note.calculatedPitch);
        }
    }

    public List<UUID> getAudience() {
        return audience;
    }
}
