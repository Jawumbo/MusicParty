package de.jawumbo.musicparty.common.bukkit.util;

public enum NBSInstrument {
    PIANO(0, "block.note_block.harp"),
    BASS(1, "block.note_block.bass"),
    BASS_DRUM(2, "block.note_block.basedrum"),
    SNARE_DRUM(3, "block.note_block.snare"),
    CLICK(4, "block.note_block.hat"),
    GUITAR(5, "block.note_block.guitar"),
    FLUTE(6, "block.note_block.flute"),
    BELL(7, "block.note_block.bell"),
    CHIME(8, "block.note_block.chime"),
    XYLOPHONE(9, "block.note_block.xylophone"),
    IRON_XYLOPHONE(10, "block.note_block.iron_xylophone"),
    COW_BELL(11, "block.note_block.cow_bell"),
    DIDGERIDOO(12, "block.note_block.didgeridoo"),
    BIT(13, "block.note_block.bit"),
    BANJO(14, "block.note_block.banjo"),
    PLING(15, "block.note_block.pling");

    private final int id;
    private final String soundName;

    NBSInstrument(int id, String soundName) {
        this.id = id;
        this.soundName = soundName;
    }

    public static String getSoundName(int id) {
        for (NBSInstrument instr : values())
            if (instr.id == id) return instr.soundName;
        return PIANO.soundName; // Fallback
    }
}
