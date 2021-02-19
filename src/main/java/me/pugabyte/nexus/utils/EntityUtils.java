package me.pugabyte.nexus.utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityUtils {

	public static LinkedHashMap<Entity, Long> getNearbyEntities(Location location, double radius) {
		return Utils.sortByValue(location.getWorld().getNearbyEntities(location, radius, radius, radius).stream()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())));
	}

	public static LinkedHashMap<EntityType, Long> getNearbyEntityTypes(Location location, double radius) {
		return Utils.sortByValue(location.getWorld().getNearbyEntities(location, radius, radius, radius).stream()
				.collect(Collectors.groupingBy(Entity::getType, Collectors.counting())));
	}

	public static List<EntityType> getExtraHostileMobs() {
		return Arrays.asList(EntityType.PHANTOM, EntityType.GHAST, EntityType.MAGMA_CUBE, EntityType.SLIME, EntityType.SHULKER);
	}
}
