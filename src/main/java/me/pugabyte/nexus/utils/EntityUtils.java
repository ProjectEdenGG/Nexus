package me.pugabyte.nexus.utils;

import me.lexikiq.HasPlayer;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityUtils {

	@NotNull
	public static LinkedHashMap<Entity, Long> getNearbyEntities(Location location, double radius) {
		if (location.getWorld() == null) return new LinkedHashMap<>();
		return Utils.sortByValue(location.getWorld().getNearbyEntities(location, radius, radius, radius).stream()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())));
	}

	public static LinkedHashMap<EntityType, Long> getNearbyEntityTypes(Location location, double radius) {
		if (location.getWorld() == null) return new LinkedHashMap<>();
		return Utils.sortByValue(location.getWorld().getNearbyEntities(location, radius, radius, radius).stream()
				.collect(Collectors.groupingBy(Entity::getType, Collectors.counting())));
	}

	public static List<EntityType> getExtraHostileMobs() {
		return Arrays.asList(EntityType.PHANTOM, EntityType.GHAST, EntityType.MAGMA_CUBE, EntityType.SLIME, EntityType.SHULKER, EntityType.ENDER_DRAGON);
	}

	public static Entity getNearestEntityType(Location location, EntityType filter, double radius) {
		if (location == null || location.getWorld() == null)
			return null;

		List<Entity> entities = getNearbyEntities(location, radius).keySet().stream()
				.filter(_entity -> _entity.getType().equals(filter))
				.filter(_entity -> !(_entity instanceof Player) || (!CitizensUtils.isNPC(_entity) && !PlayerUtils.isVanished((Player) _entity)))
				.collect(Collectors.toList());

		double shortest = radius;
		Entity result = null;
		for (Entity entity : entities) {
			double distance = entity.getLocation().distance(location);
			if (distance < shortest) {
				shortest = distance;
				result = entity;
			}
		}

		return result;
	}

	public static void makeArmorStandLookAtPlayer(ArmorStand stand, HasPlayer player, Double minYaw, Double maxYaw, Double minPitch, Double maxPitch) {
		Location origin = stand.getEyeLocation(); //our original location (Point A)
		double initYaw = origin.getYaw();
		Vector tgt = player.getPlayer().getEyeLocation().toVector(); //our target location (Point B)
		origin.setDirection(tgt.subtract(origin.toVector())); //set the origin's direction to be the direction vector between point A and B.
		double yaw = origin.getYaw() - initYaw;
		double pitch = origin.getPitch();

		if (yaw < -180)
			yaw = yaw + 360;
		else if (yaw >= 180)
			yaw -= 360;

		if (maxYaw != null && yaw > maxYaw)
			yaw = maxYaw;
		else if (minYaw != null && yaw < minYaw)
			yaw = minYaw;

		if (maxPitch != null && pitch > maxPitch)
			pitch = maxPitch;
		else if (minPitch != null && pitch < minPitch)
			pitch = minPitch;

		double x = Math.toRadians(pitch);
		double y = Math.toRadians(yaw);

		EulerAngle ea = new EulerAngle(x, y, 0);
		stand.setHeadPose(ea);
	}

	public static boolean isUnnaturalSpawn(LivingEntity entity) {
		switch (entity.getEntitySpawnReason()) {
			case SPAWNER_EGG:
			case SPAWNER:
			case CUSTOM:
				return true;
			default:
				return false;
		}
	}
}
