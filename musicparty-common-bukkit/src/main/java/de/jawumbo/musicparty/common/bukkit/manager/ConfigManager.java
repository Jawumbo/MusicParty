package de.jawumbo.musicparty.common.bukkit.manager;

import de.jawumbo.musicparty.common.bukkit.util.ConfigYML;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;

public final class ConfigManager {

    private final JavaPlugin javaPlugin;

    private ConfigYML config;

    public ConfigManager(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(Path.of(javaPlugin.getDataFolder().getPath(), "config.yml"))
                .nodeStyle(NodeStyle.BLOCK)
                .build();

        try {
            CommentedConfigurationNode rootNode = updateNode(loader.load());
            this.config = rootNode.get(ConfigYML.class, new ConfigYML());
            rootNode.set(ConfigYML.class, this.config);
            loader.save(rootNode);
        } catch (final ConfigurateException ignored) {
            javaPlugin.getLogger().warning("Could not load config.yml");
        }
    }

    public ConfigurationTransformation.Versioned create() {
        return ConfigurationTransformation.versionedBuilder()
                .addVersion(0, initialTransform())
                .build();
    }

    public ConfigurationTransformation initialTransform() {
        return ConfigurationTransformation.builder().build();
    }

    public <N extends ConfigurationNode> N updateNode(final N node) throws ConfigurateException {
        if (!node.virtual()) {
            final ConfigurationTransformation.Versioned trans = create();
            final int startVersion = trans.version(node);
            trans.apply(node);
            final int endVersion = trans.version(node);
            if (startVersion != endVersion)
                this.javaPlugin.getLogger().info("Migrated config.yml from version " + startVersion + " to " + endVersion);
        }
        return node;
    }

    public ConfigYML getConfig() {
        return config;
    }
}