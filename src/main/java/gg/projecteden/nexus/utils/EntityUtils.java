package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.Nexus;
import me.lexikiq.HasPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gg.projecteden.utils.StringUtils.camelCase;

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

	public static boolean isHostile(Entity entity) {
		return entity instanceof Monster || getExtraHostileMobs().contains(entity.getType());
	}

	public static List<EntityType> getExtraHostileMobs() {
		return Arrays.asList(EntityType.PHANTOM, EntityType.GHAST, EntityType.MAGMA_CUBE, EntityType.SLIME, EntityType.SHULKER, EntityType.ENDER_DRAGON);
	}

	public static void makeArmorStandLookAtPlayer(ArmorStand stand, HasPlayer player, Double minYaw, Double maxYaw, Double minPitch, Double maxPitch) {
		makeArmorStandLookAtPlayer(stand, player, minYaw, maxYaw, minPitch, maxPitch, null);
	}

	public static void makeArmorStandLookAtPlayer(ArmorStand stand, HasPlayer player, Double minYaw, Double maxYaw, Double minPitch, Double maxPitch, Double percent) {
		Location standLocation = stand.getEyeLocation(); // Point A
		double standYaw = standLocation.getYaw();

		Vector playerLocation = player.getPlayer().getEyeLocation().toVector(); // Point B

		//set the origin's direction to be the direction vector between point A and B.
		if (percent == null) {
			standLocation.setDirection(playerLocation.subtract(standLocation.toVector()));
		}
		else {
			Vector standLookVector = stand.getEyeLocation().getDirection().multiply(.3);
			List<Vector> vecs = new SplinePath(1f, percent, standLookVector, playerLocation).getPath();
			if (vecs.size() >= 2) {
				standLocation.setDirection(vecs.get(1));
			}
		}

		double yaw = standLocation.getYaw();
		double pitch = standLocation.getPitch();

		if (yaw < -180)
			yaw = yaw + 360;
		else if (yaw >= 180)
			yaw -= 360;
//
//		if (maxYaw != null && yaw > maxYaw)
//			yaw = maxYaw;
//		if (minYaw != null && yaw < minYaw)
//			yaw = minYaw;
//
//		if (maxPitch != null && pitch > maxPitch)
//			pitch = maxPitch;
//		if (minPitch != null && pitch < minPitch)
//			pitch = minPitch;

		Location loc = stand.getLocation().clone();
		loc.setPitch((float) pitch);
		loc.setYaw((float) yaw);
		stand.teleport(loc);
	}

	public static boolean isUnnaturalSpawn(LivingEntity entity) {
		EntityType type = entity.getType();
		SpawnReason reason = entity.getEntitySpawnReason();

		// Special cases
		if (type.equals(EntityType.CAVE_SPIDER) && reason.equals(SpawnReason.SPAWNER))
			return false;
		//

		return switch (entity.getEntitySpawnReason()) {
			case SPAWNER_EGG, SPAWNER, CUSTOM, BUILD_IRONGOLEM, COMMAND -> true;
			default -> false;
		};
	}

	public static EntityType getSpawnEggType(Material type) {
		return EntityType.valueOf(type.toString().split("_SPAWN_EGG")[0]);
	}

	public static Material getSpawnEgg(EntityType type) {
		return Material.valueOf(type.toString() + "_SPAWN_EGG");
	}

	public static double setHealth(LivingEntity entity, double health) {
		final AttributeInstance attribute = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		if (attribute == null) {
			Nexus.warn("Could not find max health attribute on " + camelCase(entity.getType()));
			return entity.getHealth();
		}

		attribute.setBaseValue(health);
		entity.setHealth(health);
		return health;
	}

}
