package gg.projecteden.nexus.features.resourcepack.decoration.common;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Seat extends Decoration {
	private static final String id = "DecorationSeat";

	public void trySit(Player player, Location location) {
		location = location.toCenterLocation().subtract(0, 0.9, 0);
		if (!canSit(player, location))
			return;

		makeSit(player, location);
	}

	public void makeSit(Player player, Location location) {
		World world = location.getWorld();
		ArmorStand armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
		armorStand.setInvulnerable(true);
		armorStand.setGravity(false);
		armorStand.setSmall(true);
		armorStand.setCustomName(id + "-" + player.getUniqueId());
		armorStand.setCustomNameVisible(false);

		armorStand.addPassenger(player);
	}

	public static boolean canSit(Player player, Location location) {
		if (isSitting(player))
			return false;

		return !isOccupied(location);
	}

	public static boolean isOccupied(Location location) {
		return location.getNearbyEntitiesByType(ArmorStand.class, 0.5).stream()
			.anyMatch(armorStand -> armorStand.getPassengers().size() > 0);
	}

	public static boolean isSitting(Player player) {
		if (!player.isInsideVehicle())
			return false;

		if (!(player.getVehicle() instanceof ArmorStand armorStand))
			return false;

		return isSeat(armorStand);
	}

	public static boolean isSeat(ArmorStand armorStand) {
		String customName = armorStand.getCustomName();
		return customName != null && armorStand.getCustomName().contains(id);
	}
}
