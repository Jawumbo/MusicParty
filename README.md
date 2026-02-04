# MusicParty

**Listen, Guess and Win!**  
MusicParty is a free & open-source Minecraft **music quiz** plugin for **Paper** and **Spigot**: the plugin plays a
song (from `.nbs` files), then you have to guess the correct track from 4 options via a GUI.

Repository: https://github.com/Jawumbo/MusicParty
Modrinth: https://modrinth.com/plugin/musicparty
CurseForge: https://www.curseforge.com/minecraft/bukkit-plugins/musicparty
---

## Features

- **Singleplayer music quiz**
- Plays songs from **Note Block Studio** (`.nbs`) files
- Clean **GUI** flow:
    1. Song plays
    2. Voting GUI with 4 choices
    3. Result GUI (next round / quit)
- Works on both **Paper** and **Spigot**
- **bStats metrics** integration (plugin id **29172**)  
  Stats page: https://bstats.org/ (bStats platform overview [[3]](https://bstats.org/))

> Future ideas (not fully implemented yet): multiplayer, stats, rewards/punishments, categories/genres, localization.

---

## Requirements

- **Java 21**
- A Paper or Spigot server compatible with your build (the project targets modern Bukkit API versions)

---

## Installation

1. Download the latest release jar from GitHub Releases (recommended), or build it yourself (see below).
2. Put the jar into your server’s `plugins/` folder.
3. Start the server once.
4. Add songs:
    - Go to `plugins/MusicParty/songs/`
    - Drop at least **4** `.nbs` files into that folder
5. Restart the server (or reload if you know what you’re doing).

> If there are fewer than 4 songs available, you won’t be able to start a game.

---

## Usage

### Commands

- `/musicparty` — show plugin info
- `/musicparty help` — show all commands
- `/musicparty join` — start/join **singleplayer** mode
- `/musicparty leave` — leave the game

Alias: `/mp`

### Permission

- `musicparty.use`  
  Default: **OP**

---

## Adding Songs (`.nbs`)

MusicParty reads `.nbs` files from:

- `plugins/MusicParty/songs/`

Tips:

- Use Note Block Studio to create/export `.nbs` songs.
- Keep filenames and metadata tidy—song title/author are shown in the GUI.

---

## bStats (Metrics)

MusicParty includes **bStats** to help track anonymous usage statistics.

- Plugin id: **29172**
- bStats project page: https://bstats.org/plugin/bukkit/MusicParty/29172
- You can control metrics globally via the standard `bStats` configuration on your server (see bStats
  site [[3]](https://bstats.org/)).

---

## Building from Source (Maven)

Clone the repository and run: `bash mvn -DskipTests package`

You’ll get shaded jars for the platform modules in their `target/` folders.

---

## Contributing

Issues and pull requests are welcome.

- Bug reports: include server type (Paper/Spigot), version, Java version, and steps to reproduce.
- Feature requests: describe the gameplay goal and expected UX.

---

## License

This project is licensed under the **GNU GPL v3.0**.  
See [`LICENSE`](LICENSE) for details.