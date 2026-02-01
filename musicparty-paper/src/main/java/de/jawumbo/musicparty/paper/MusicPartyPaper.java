package de.jawumbo.musicparty.paper;

import de.jawumbo.musicparty.common.bukkit.MusicPartyCommonBukkit;
import de.jawumbo.musicparty.paper.commands.MusicPartyCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class MusicPartyPaper extends JavaPlugin {

    private MusicPartyCommonBukkit musicPartyCommonBukkit;

    @Override
    public void onLoad() {
        this.musicPartyCommonBukkit = new MusicPartyCommonBukkit(this);
    }

    @Override
    public void onEnable() {
        this.musicPartyCommonBukkit.onEnable();

        new MusicPartyCommand(this, this.musicPartyCommonBukkit.getGameManager(), this.musicPartyCommonBukkit.getSongManager());
    }

    @Override
    public void onDisable() {
        this.musicPartyCommonBukkit.onDisable();
    }
}
