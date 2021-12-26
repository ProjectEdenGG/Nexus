package gg.projecteden.nexus.features.resourcepack.decoration.common;

import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class Seat extends Decoration {
	private static final String id = "DecorationSeat";

	public void trySit(Player player, ItemFrame itemFrame) {
		Location location = itemFrame.getLocation().toCenterLocation().clone().subtract(0, 0.9, 0);
		if (!canSit(player, location))
			return;

		makeSit(player, location, itemFrame.getRotation());
	}

	public void makeSit(Player player, Location location, Rotation rotation) {
		World world = location.getWorld();
		location.setYaw(getYaw(rotation));

		ArmorStand armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
		armorStand.setInvulnerable(true);
		armorStand.setGravity(false);
		armorStand.setSmall(true);
		armorStand.setCustomName(id + "-" + player.getUniqueId());
		armorStand.setCustomNameVisible(false);
		armorStand.setVisible(false);

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

	private static float getYaw(Rotation rotation) {
		BlockFace blockFace = ItemFrameRotation.from(rotation).getBlockFace().getOppositeFace();
		List<BlockFace> radial = Arrays.asList(BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST, BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST);
		int ndx = radial.indexOf(blockFace);
		return ndx * 45F;
	}
}
