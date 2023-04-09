package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.framework.exceptions.NexusException;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class WorldUtils {

	public static int getMobSpawnRange(World world) {
		return getWorldSetting(world, "mob-spawn-range");
	}

	public static int getWorldSetting(World world, String setting) {
		ConfigurationSection section = Bukkit.getServer().spigot().getSpigotConfig().getConfigurationSection("world-settings");
		if (section == null)
			throw new NexusException("Could not find `world-settings` section in spigot.yml");

		ConfigurationSection worldSection = section.getConfigurationSection(world.getName());
		if (worldSection != null)
			if (worldSection.contains(setting))
				return worldSection.getInt(setting);

		ConfigurationSection defaultSection = section.getConfigurationSection("default");
		if (defaultSection == null)
			throw new NexusException("Could not find `world-settings.default` section in spigot.yml");

		if (!defaultSection.contains(setting))
			throw new NexusException("Could not find `world-settings.default." + setting + "` in spigot.yml");

		return defaultSection.getInt(setting);
	}

	@NotNull
	public static Location getRandomLocationInBorder(World world) {
		final WorldBorder border = world.getWorldBorder();
		final Supplier<Double> randomDouble = () -> RandomUtils.randomDouble(-border.getSize() / 2, border.getSize() / 2);
		return border.getCenter().add(randomDouble.get(), 0, randomDouble.get());
	}

	@AllArgsConstructor
	public enum TimeQuadrant {
		MORNING(0, 2000),
		DAY(2000, 12000),
		EVENING(12000, 14000),
		NIGHT(14000, 24000),
		;

		private final int minTime;
		private final int maxTime;

		public static TimeQuadrant of(World world) {
			return of(world.getTime());
		}

		public static TimeQuadrant of(long time) {
			for (TimeQuadrant quadrant : values()) {
				if (time >= quadrant.minTime && time < quadrant.maxTime)
					return quadrant;
			}

			return null;
		}
	}

	public static boolean isInWorldBorder(World world, Chunk chunk) {
		int y = 0;
		List<Location> corners = new ArrayList<>();

		corners.add(new Location(world, (chunk.getX() << 4), y, (chunk.getZ() << 4)));
		corners.add(new Location(world, (chunk.getX() << 4), y, (chunk.getZ() << 4) + 15));
		corners.add(new Location(world, (chunk.getX() << 4) + 15, y, (chunk.getZ() << 4)));
		corners.add(new Location(world, (chunk.getX() << 4) + 15, y, (chunk.getZ() << 4) + 15));

		for (Location corner : corners) {
			if (!world.getWorldBorder().isInside(corner))
				return false;
		}
		return true;
	}
}
