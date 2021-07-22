package gg.projecteden.nexus.utils;

import me.lexikiq.HasPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
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

	public static boolean isHostile(Entity entity) {
		return entity instanceof Monster || getExtraHostileMobs().contains(entity.getType());
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
		makeArmorStandLookAtPlayer(stand, player, minYaw, maxYaw, minPitch, maxPitch, null);
	}

	public static void makeArmorStandLookAtPlayer(ArmorStand stand, HasPlayer player, Double minYaw, Double maxYaw, Double minPitch, Double maxPitch, Double percent) {
		Location standLocation = stand.getEyeLocation(); // Point A
		double standYaw = standLocation.getYaw();

		Vector playerLocation = player.getPlayer().getEyeLocation().toVector(); // Point B

		//set the origin's direction to be the direction vector between point A and B.
		if (percent == null)
			standLocation.setDirection(playerLocation.subtract(standLocation.toVector()));
		else {
//			Nexus.log("Old Dir: " + standLocation.getDirection());

			Vector diff = playerLocation.subtract(standLocation.toVector());
//			Nexus.log("diff: " + diff);

			Vector percentage = diff.multiply(percent);
//			Nexus.log(percent + "% = " + percentage);

			standLocation.setDirection(percentage);
//			Nexus.log("New Dir: " + standLocation.getDirection());
		}

		double yaw = standLocation.getYaw() - standYaw;
		double pitch = standLocation.getPitch();

		if (yaw < -180)
			yaw = yaw + 360;
		else if (yaw >= 180)
			yaw -= 360;

		if (maxYaw != null)
			yaw = Math.min(yaw, maxYaw);
		if (minYaw != null)
			yaw = Math.max(yaw, minYaw);

		if (maxPitch != null)
			pitch = Math.min(pitch, maxPitch);
		if (minPitch != null)
			pitch = Math.max(pitch, minPitch);

		double x = Math.toRadians(pitch);
		double y = Math.toRadians(yaw);

		EulerAngle ea = new EulerAngle(x, y, 0);
		stand.setHeadPose(ea);
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

}
