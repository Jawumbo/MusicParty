package de.jawumbo.musicparty.common.bukkit.util;

public class NBSLocation {
    private final float yaw;
    public double x, z;

    public NBSLocation(double x, double z, float yaw) {
        this.x = x;
        this.z = z;
        this.yaw = yaw;
    }

    public static void calculateNoteLocation(NBSLocation location, NBSFile.Note note) {
        // NBS Panning: 0 = far left, 100 = center, 200 = far right
        if (note.panning != 100) {
            // Convert to a range from -1.0 (left) to 1.0 (right)
            double panValue = (note.panning - 100) / 100.0;

            // The distance at which the sound should be placed (e.g., 2 blocks to the side)
            double distance = 2.0 * panValue;

            // In Minecraft, Yaw 0 is south, 90 is west, etc.
            // We calculate the angle for the "side", which is yaw + 90 degrees
            double radians = Math.toRadians(location.yaw + 90);

            // Calculate the X and Z offset based on the angle
            // We use -sin for X and cos for Z to get the right vector
            double dx = -Math.sin(radians) * distance;
            double dz = Math.cos(radians) * distance;

            location.add(dx, dz);
        }
    }

    public NBSLocation add(double x, double z) {
        this.x += x;
        this.z += z;
        return this;
    }
}
