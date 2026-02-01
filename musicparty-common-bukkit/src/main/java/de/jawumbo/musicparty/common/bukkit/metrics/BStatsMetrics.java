package de.jawumbo.musicparty.common.bukkit.metrics;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public record BStatsMetrics(Metrics metrics) {

    public BStatsMetrics(JavaPlugin metrics) {
        this(new Metrics(metrics, 29172));
    }
}
