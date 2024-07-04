package gg.projecteden.nexus.utils;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTEntity;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.parchment.HasPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.Distance.distance;
import static gg.projecteden.nexus.utils.StringUtils.camelCase;

public class EntityUtils {

	public static void forcePacket(Entity entity) {
		((CraftEntity) entity).getHandle().hasImpulse = true; // hasImpulse = true
	}

	@NotNull
	public static LinkedHashMap<Entity, Long> getNearbyEntities(Location location, double radius) {
		if (location.getWorld() == null) return new LinkedHashMap<>();
		return Utils.sortByValue(location.getWorld().getNearbyEntities(location, radius, radius, radius).stream()
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())));
	}

	public static LinkedHashMap<EntityType, Long> getNearbyEntityTypes(Location location, double radius) {
		if (location.getWorld() == null) return new LinkedHashMap<>();
		return Utils.sortByValue(location.getWorld().getNearbyEntities(location, radius, radius, radius).stream()
			.collect(Collectors.groupingBy(entity -> {
				if (CitizensUtils.isNPC(entity))
					return EntityType.NPC;
				else
					return entity.getType();
			}, Collectors.counting())));
	}

	@NotNull
	public static Optional<Entity> getNearestEntity(Location location, int radius) {
		return location.getNearbyEntities(radius, radius, radius).stream()
			.filter(entity -> entity.getType() != EntityType.PLAYER)
			.sorted(Comparator.comparing(entity -> distance(location, entity).get()))
			.findFirst();
	}

	public static boolean isHostile(Entity entity) {
		return entity instanceof Monster || getExtraHostileMobs().contains(entity.getType());
	}

	public static List<EntityType> getExtraHostileMobs() {
		return Arrays.asList(EntityType.PHANTOM, EntityType.GHAST, EntityType.MAGMA_CUBE, EntityType.SLIME, EntityType.SHULKER, EntityType.ENDER_DRAGON, EntityType.SKELETON_HORSE);
	}

	public static void makeArmorStandLookAtPlayer(ArmorStand stand, HasPlayer player, Double minYaw, Double maxYaw, Double minPitch, Double maxPitch) {
		Location standLocation = stand.getEyeLocation(); // Point A
		double standYaw = standLocation.getYaw();

		Vector playerLocation = player.getPlayer().getEyeLocation().toVector(); // Point B

		//set the origin's direction to be the direction vector between point A and B.
		standLocation.setDirection(playerLocation.subtract(standLocation.toVector()));

		double yaw = standLocation.getYaw() - standYaw;
		double pitch = standLocation.getPitch();

		if (yaw < -180)
			yaw = yaw + 360;
		else if (yaw >= 180)
			yaw -= 360;

		if (maxYaw != null && yaw > maxYaw)
			yaw = maxYaw;
		if (minYaw != null && yaw < minYaw)
			yaw = minYaw;

		if (maxPitch != null && pitch > maxPitch)
			pitch = maxPitch;
		if (minPitch != null && pitch < minPitch)
			pitch = minPitch;

		double x = Math.toRadians(pitch);
		double y = Math.toRadians(yaw);

		EulerAngle ea = new EulerAngle(x, y, 0);
		stand.setHeadPose(ea);
	}

	public static void makeArmorStandLookAtPlayer(ArmorStand stand, HasPlayer player, Double minYaw, Double maxYaw, Double minPitch, Double maxPitch, Double percent) {
		if (percent == null) {
			makeArmorStandLookAtPlayer(stand, player, minYaw, maxYaw, minPitch, maxPitch);
			return;
		}

		Location standLocation = stand.getEyeLocation(); // Point A
		Vector playerLocation = player.getPlayer().getEyeLocation().toVector(); // Point B

		Vector standLookVector = stand.getEyeLocation().getDirection().multiply(.3);
		List<Vector> vecs = new SplinePath(1f, percent, standLookVector, playerLocation).getPath();
		if (vecs.size() >= 2) {
			standLocation.setDirection(vecs.get(1));
		}

		double yaw = standLocation.getYaw();
		double pitch = standLocation.getPitch();

		if (yaw < -180)
			yaw = yaw + 360;
		else if (yaw >= 180)
			yaw -= 360;

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

	public static Entity cloneEntity(Entity entity) {
		Entity newEntity = entity.getWorld().spawnEntity(entity.getLocation(), entity.getType());

		final NBTEntity entityNbt = new NBTEntity(entity);
		final NBTEntity newEntityNbt = new NBTEntity(newEntity);

		newEntityNbt.mergeCompound(new NBTContainer() {{
			mergeCompound(entityNbt);
			removeKey("UUID");
		}});

		return newEntity;
	}

	public static Vector getForcefieldVelocity(Entity toPush, Location fromLoc) {
		return getForcefieldVelocity(toPush, fromLoc, 0.5);
	}

	public static Vector getForcefieldVelocity(Entity toPush, Location fromLoc, double yVel) {
		Location entityLoc = toPush.getLocation();
		Vector entityDir = entityLoc.getDirection();

		fromLoc.setDirection(entityDir);

		Vector launchDirection = entityLoc.toVector().add(fromLoc.toVector().multiply(-1)).normalize();
		launchDirection.setY(yVel);

		if (toPush instanceof Item)
			launchDirection.multiply(0.5);

		if (toPush instanceof Player player && player.isGliding()) {
			player.setGliding(false);
		}

		return launchDirection;
	}

}
