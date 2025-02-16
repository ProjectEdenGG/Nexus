package gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.DecorationError;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Couch;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Couch.CouchPart;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface Seat extends Interactable {
	double SIT_HEIGHT = 1.0;
	String id = "DecorationSeat";
	List<BlockFace> radialFaces = Arrays.asList(BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST, BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST);
	List<Material> ignoredMaterials = new ArrayList<>() {{
		addAll(MaterialTag.ALL_AIR.getValues());
		add(Material.LIGHT);
		addAll(MaterialTag.ALL_SIGNS.getValues());
	}};

	default double getSitHeight() {
		return SIT_HEIGHT;
	}

	default boolean isBackless() {
		return false;
	}

	default ArmorStand trySit(Player player, ItemFrame itemFrame, DecorationConfig config) {
		return trySit(player, itemFrame.getLocation().getBlock(), itemFrame.getRotation(), config);
	}

	default ArmorStand trySit(Player player, Block block, Rotation rotation, DecorationConfig config) {
		Location location = block.getLocation().toCenterLocation().clone().add(0, -1 + getSitHeight(), 0);

		if (!canSit(player, location))
			return null;

		return makeSit(player, location, rotation, config);
	}

	default ArmorStand makeSit(Player player, Location location, Rotation rotation, DecorationConfig config) {
		World world = location.getWorld();
		float yaw = getYaw(rotation);

		if (config instanceof Couch couch) {
			if (couch.getCouchPart().equals(CouchPart.CORNER))
				yaw += 45;
		}

		location.setYaw(yaw);

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

		if (armorStand.isValid()) {
			armorStand.addPassenger(player);
			DecorationLang.debug(player, "sat down");
		} else {
			DecorationLang.debug(player, "&cfailed to sit down, armorstand is invalid");
		}

		return armorStand;
	}


	private float getYaw(Rotation rotation) {
		BlockFace blockFace = ItemFrameRotation.of(rotation).getBlockFace().getOppositeFace();
		int ndx = radialFaces.indexOf(blockFace);
		return ndx * 45F;
	}

	default boolean canSit(Player player, Location location) {
		if (isSitting(player)) {
			DecorationLang.debug(player, "player is already sitting");
			return false;
		}

		if (isOccupied(location)) {
			DecorationLang.debug(player, "seat location is occupied");
			DecorationError.SEAT_OCCUPIED.send(player);
			return false;
		}

		Block aboveBlock = location.getBlock().getRelative(BlockFace.UP);
		Material aboveMaterial = aboveBlock.getType();

		if (ignoredMaterials.contains(aboveMaterial))
			return true;

		if (!aboveBlock.isSolid())
			return true;

		if (!aboveMaterial.isBlock())
			return true;

		DecorationLang.debug(player, "above seat location is not safe");

		return false;
	}

	default boolean isOccupied(@NonNull Location location) {
		return location.toCenterLocation().getNearbyEntitiesByType(ArmorStand.class, 0.5).stream()
			.anyMatch(armorStand -> armorStand.getPassengers().size() > 0);
	}

	default boolean isOccupied(@NonNull DecorationConfig config, @NonNull ItemFrame itemFrame, Player debugger) {
		if (!config.isMultiBlock()) {
			boolean occupied = isOccupied(itemFrame.getLocation());
			DecorationLang.debug(debugger, "is multiblock seat, is occupied: " + occupied);
			return occupied;
		}

		List<Hitbox> hitboxes = Hitbox.rotateHitboxes(config, itemFrame);
		for (Hitbox hitbox : hitboxes) {
			Block offsetBlock = hitbox.getOffsetBlock(itemFrame.getLocation());
			if (isOccupied(offsetBlock.getLocation())) {
				DecorationLang.debug(debugger, "is occupied");
				return true;
			}
		}

		DecorationLang.debug(debugger, "is not occupied");
		return false;
	}

	default boolean isSitting(Player player) {
		if (!player.isInsideVehicle()) {
			DecorationLang.debug(player, "player is not inside vehicle");
			return false;
		}

		if (!(player.getVehicle() instanceof ArmorStand armorStand)) {
			DecorationLang.debug(player, "player is not on an armorstand");
			return false;
		}

		return isSeat(armorStand);
	}

	static boolean isSeat(ArmorStand armorStand) {
		String customName = armorStand.getCustomName();
		return customName != null && armorStand.getCustomName().contains(id);
	}

	static void dismount(Player player, ArmorStand armorStand) {
		Tasks.cancel(Decoration.getBacklessTasks().remove(player.getUniqueId()));
		double yDiff = armorStand.getLocation().getY() - armorStand.getLocation().getBlockY();
		armorStand.remove();
		player.teleport(player.getLocation().add(0, 1 + yDiff, 0));
	}
}
