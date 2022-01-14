package gg.projecteden.nexus.features.resourcepack.decoration.common;

import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Arrays;
import java.util.List;

public interface Seat {
	String id = "DecorationSeat";

	default void trySit(Player player, Block block, Rotation rotation) {
		Location location = block.getLocation().toCenterLocation().clone().subtract(0, 0.2, 0);
		if (!canSit(player, location))
			return;

		makeSit(player, location, rotation);
	}

	default void makeSit(Player player, Location location, Rotation rotation) {
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

	private float getYaw(Rotation rotation) {
		BlockFace blockFace = ItemFrameRotation.from(rotation).getBlockFace().getOppositeFace();
		List<BlockFace> radial = Arrays.asList(BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST, BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST);
		int ndx = radial.indexOf(blockFace);
		return ndx * 45F;
	}

	default boolean canSit(Player player, Location location) {
		if (isSitting(player))
			return false;

		return !isOccupied(location);
	}

	default boolean isOccupied(@NonNull Location location) {
		return location.toCenterLocation().getNearbyEntitiesByType(ArmorStand.class, 0.5).stream()
			.anyMatch(armorStand -> armorStand.getPassengers().size() > 0);
	}

	default boolean isOccupied(@NonNull Decoration decoration, @NonNull ItemFrame itemFrame) {
		if (!decoration.isMultiBlock())
			return isOccupied(itemFrame.getLocation());

		List<Hitbox> hitboxes = Hitbox.getHitboxes(decoration, itemFrame);
		for (Hitbox hitbox : hitboxes) {
			Block offsetBlock = hitbox.getOffsetBlock(itemFrame.getLocation());
			if (isOccupied(offsetBlock.getLocation()))
				return true;
		}

		return false;
	}

	default boolean isSitting(Player player) {
		if (!player.isInsideVehicle())
			return false;

		if (!(player.getVehicle() instanceof ArmorStand armorStand))
			return false;

		return isSeat(armorStand);
	}

	static boolean isSeat(ArmorStand armorStand) {
		String customName = armorStand.getCustomName();
		return customName != null && armorStand.getCustomName().contains(id);
	}

	@AllArgsConstructor
	enum DyedPart {
		WHOLE(Decoration.getDefaultWoodColor()),
		CUSHION(Color.RED),
		;

		@Getter
		final Color defaultColor;
	}
}
