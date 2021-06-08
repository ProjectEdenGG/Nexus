package me.pugabyte.nexus.utils;

import me.pugabyte.nexus.framework.exceptions.NexusException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

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
		double radius = world.getWorldBorder().getSize();
		double x = RandomUtils.randomDouble(-radius / 2, radius / 2);
		double z = RandomUtils.randomDouble(-radius / 2, radius / 2);

		Location center = world.getWorldBorder().getCenter();
		return new Location(world, x, 0, z).add(center.getX(), 0, center.getZ());
	}

}
