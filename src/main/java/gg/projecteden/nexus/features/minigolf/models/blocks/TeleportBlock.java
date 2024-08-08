package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TeleportBlock extends ModifierBlock {

	@Override
	public ColorType getDebugDotColor() {
		return ColorType.GREEN;
	}

	@Override
	public void handleBounce(GolfBall golfBall, Block block, BlockFace blockFace) {
		if (blockFace.equals(BlockFace.UP)) {
			handleRoll(golfBall, block);
			return;
		}

		super.handleBounce(golfBall, block, blockFace);
	}

	@Override
	public void handleRoll(GolfBall golfBall, Block below) {
		rollDebug(golfBall);

		org.bukkit.block.Dispenser dispenser = (org.bukkit.block.Dispenser) below.getState();
		TeleportBlockArgs teleportLocation = getTeleportLocation(golfBall, dispenser);
		if (teleportLocation == null) {
			golfBall.debug("teleport location == null");
			super.handleRoll(golfBall, below);
			return;
		}

		golfBall.debug("teleporting ball...");
		Location newLoc = teleportLocation.getLocation();

		golfBall.setVelocity(new Vector(0, 0, 0));
		golfBall.teleport(newLoc);
		golfBall.setVelocity(newLoc.getDirection().multiply(teleportLocation.getSpeed()));
		golfBall.setGravity(true);

	}

	@Override
	public Set<Material> getMaterials() {
		return Set.of(Material.DISPENSER);
	}

	@SuppressWarnings("deprecation")
	private @Nullable TeleportBlock.TeleportBlockArgs getTeleportLocation(GolfBall golfBall, Dispenser dispenser) {

		ItemStack[] contents = dispenser.getInventory().getContents();
		if (contents.length == 0) {
			golfBall.debug("contents is empty");
			return null;
		}

		List<TeleportBlockArgs> teleportLocations = new ArrayList<>();

		for (ItemStack item : contents) {
			if (Nullables.isNullOrAir(item) || item.getType() != Material.PAPER || (!item.hasItemMeta()))
				continue;

			ItemMeta itemMeta = item.getItemMeta();
			if (!itemMeta.hasDisplayName() || !StringUtils.stripColor(itemMeta.getDisplayName()).equalsIgnoreCase(TeleportBlockArgs.ITEM_NAME))
				continue;

			List<String> lore = itemMeta.getLore();
			if (Nullables.isNullOrEmpty(lore))
				continue;

			TeleportBlockArgs args = TeleportBlockArgs.fromLore(golfBall, lore);
			if (args == null)
				continue;

			teleportLocations.add(args);
		}

		if (teleportLocations.isEmpty()) {
			golfBall.debug("list is empty");
			return null;
		}

		return RandomUtils.randomElement(teleportLocations);
	}

	@Setter
	@Getter
	@AllArgsConstructor
	public static class TeleportBlockArgs {
		public static final String ITEM_NAME = "Teleport";
		GolfBall golfBall;
		double x;
		double y;
		double z;
		float yaw;
		float pitch;
		double speed;

		public Location getLocation() {
			World world = golfBall.getLocation().getWorld();
			return new Location(world, x, y, z, yaw, pitch);
		}

		public static TeleportBlockArgs fromLore(GolfBall golfBall, List<String> lore) {
			try {
				double x = Double.parseDouble(lore.get(0));
				double y = Double.parseDouble(lore.get(1));
				double z = Double.parseDouble(lore.get(2));
				double yaw = Double.parseDouble(lore.get(3));
				double pitch = Double.parseDouble(lore.get(4));
				double speed = Double.parseDouble(lore.get(5));

				return new TeleportBlockArgs(golfBall, x, y, z, (float) yaw, (float) pitch, speed);
			} catch (Exception ignored) {
			}

			return null;
		}

		public static ItemStack getItem(Block block, Directional directional, Double speed) {
			if (speed == null)
				speed = 0.0;

			BlockFace facing = directional.getFacing();
			Block facingBlock = block.getRelative(facing);
			Location facingLocation = facingBlock.getLocation();

			double x, y, z, yaw, pitch;

			switch (facing) {
				case UP -> {
					x = facingLocation.getBlockX() + 0.5;
					z = facingLocation.getBlockZ() + 0.5;
					y = facingLocation.getBlockY() + 0.3;
					yaw = 0;
					pitch = -90.0;
				}

				case DOWN -> {
					x = facingLocation.getBlockX() + 0.5;
					z = facingLocation.getBlockZ() + 0.5;
					y = facingLocation.getBlockY() + 0.7;
					yaw = 0;
					pitch = 90.0;
				}

				case NORTH -> {
					x = facingLocation.getBlockX() + 0.5;
					z = facingLocation.getBlockZ() + 0.3;
					y = facingLocation.getBlockY() + 0.3;
					yaw = LocationUtils.getYaw(facing);
					pitch = 0;
				}

				case SOUTH -> {
					x = facingLocation.getBlockX() + 0.3;
					z = facingLocation.getBlockZ() + 0.5;
					y = facingLocation.getBlockY() + 0.3;
					yaw = LocationUtils.getYaw(facing);
					pitch = 0;
				}

				case EAST -> {
					x = facingLocation.getBlockX() + 0.7;
					z = facingLocation.getBlockZ() + 0.5;
					y = facingLocation.getBlockY() + 0.3;
					yaw = LocationUtils.getYaw(facing);
					pitch = 0;
				}

				case WEST -> {
					x = facingLocation.getBlockX() + 0.5;
					z = facingLocation.getBlockZ() + 0.7;
					y = facingLocation.getBlockY() + 0.3;
					yaw = LocationUtils.getYaw(facing);
					pitch = 0;
				}

				default -> {
					return null;
				}
			}

			List<String> lore = new ArrayList<>();
			lore.add(x + "");
			lore.add(y + "");
			lore.add(z + "");
			lore.add(yaw + "");
			lore.add(pitch + "");
			lore.add(speed + "");

			return new ItemBuilder(Material.PAPER).name(ITEM_NAME).lore(lore).build();
		}
	}
}
