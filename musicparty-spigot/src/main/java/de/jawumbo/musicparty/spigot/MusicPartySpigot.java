package de.jawumbo.musicparty.spigot;

import de.jawumbo.musicparty.common.bukkit.MusicPartyCommonBukkit;
import de.jawumbo.musicparty.spigot.commands.MusicPartyCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class MusicPartySpigot extends JavaPlugin {

    private MusicPartyCommonBukkit musicPartyCommonBukkit;

    @Override
    public void onLoad() {
        this.musicPartyCommonBukkit = new MusicPartyCommonBukkit(this);
    }

    @Override
    public void onEnable() {
        this.musicPartyCommonBukkit.onEnable();

        new MusicPartyCommand(
                this,
                this.musicPartyCommonBukkit.getConfigManager(),
                this.musicPartyCommonBukkit.getGameManager(),
                this.musicPartyCommonBukkit.getSongManager()
        );
    }

    @Override
    public void onDisable() {
        this.musicPartyCommonBukkit.onDisable();
    }
}
