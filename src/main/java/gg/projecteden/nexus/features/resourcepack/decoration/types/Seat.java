package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DisabledPlacement;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Arrays;
import java.util.List;

public class Seat extends Decoration {
	private static final String id = "DecorationSeat";

	public Seat(String name, int modelData, List<Hitbox> hitboxes) {
		this.name = name;
		this.modelData = modelData;
		this.hitboxes = hitboxes;
		this.material = Material.LEATHER_HORSE_ARMOR;
		this.disabledPlacements = List.of(DisabledPlacement.WALL, DisabledPlacement.CEILING);
	}

	public void trySit(Player player, Block block, Rotation rotation) {
		Location location = block.getLocation().toCenterLocation().clone().subtract(0, 0.2, 0);
		if (!canSit(player, location))
			return;

		makeSit(player, location, rotation);
	}

	public void makeSit(Player player, Location location, Rotation rotation) {
		World world = location.getWorld();
		location.setYaw(getYaw(rotation));

		ArmorStand armorStand = world.spawn(location, ArmorStand.class, _armorStand -> {
			_armorStand.setMarker(true);
			_armorStand.setVisible(false);
			_armorStand.setCustomNameVisible(false);
			_armorStand.setCustomName(id + "-" + player.getUniqueId());
			_armorStand.setInvulnerable(true);
			_armorStand.setGravity(false);
			_armorStand.setSmall(true);
			_armorStand.setBasePlate(true);
			_armorStand.setDisabledSlots(EquipmentSlot.values());
		});

		if (armorStand.isValid())
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
