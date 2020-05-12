package me.pugabyte.bncore.features.holidays.bearfair20.fairgrounds;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.features.particles.effects.DotEffect;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldEditUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;

public class Reflection implements Listener {

	private String gameRg = BearFair20.mainRg + "_reflection";
	private String powderRg = gameRg + "_powder";
	private boolean active = false;
	private int taskId;
	private Location laserStart;
	private List<Location> lampLocList = new ArrayList<>();

	public Reflection() {
		BNCore.registerListener(this);
		setLamps();
	}

	private void setLamps() {
		WorldEditUtils WEUtils = new WorldEditUtils(BearFair20.world);
		ProtectedRegion region = WGUtils.getProtectedRegion(powderRg);
		List<Block> blocks = WEUtils.getBlocks(region);
		for (Block block : blocks) {
			if (block.getType().equals(Material.YELLOW_CONCRETE_POWDER)) {
				Location loc = block.getRelative(0, 3, 0).getLocation();
				lampLocList.add(loc);
			}
		}
	}

	private void clearLamps() {
		for (Location lampLoc : lampLocList) {
			Block lamp = lampLoc.getBlock();
			BlockData blockData = lamp.getBlockData();
			Lightable lightable = (Lightable) blockData;
			lightable.setLit(false);
			lamp.setBlockData(lightable);
		}
	}

	@EventHandler
	public void onButtonPress(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null) return;
		if (event.getHand() == null) return;
		if (event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
		if (!event.getClickedBlock().getType().equals(Material.STONE_BUTTON)) return;

		Block block = event.getClickedBlock();
		Location loc = block.getLocation();
		if (!WGUtils.getRegionNamesAt(loc).contains(gameRg)) return;

		BlockData blockData = block.getBlockData();
		Directional directional = (Directional) blockData;
		Block powder = block.getRelative(0, -1, 0).getRelative(directional.getFacing().getOppositeFace());
		Material powderType = powder.getType();
		if (!MaterialTag.CONCRETE_POWDERS.isTagged(powderType)) return;

		if (!powderType.equals(Material.WHITE_CONCRETE_POWDER)) {
			Block banner = powder.getRelative(0, 2, 0);
			rotateBanner(banner, powderType);
		} else {
			if (!active) {
				Location start = Utils.getCenteredLocation(powder.getRelative(0, 3, 0).getLocation());
				start.setY(start.getY() + 0.25);
				laserStart = start;
				startLaser(event.getPlayer());
			}
		}
	}

	private void rotateBanner(Block banner, Material powder) {
		BlockData blockData = banner.getBlockData();
		Rotatable rotatable = (Rotatable) blockData;
		rotatable.setRotation(rotateBlockFace(rotatable.getRotation(), powder));
		banner.setBlockData(rotatable);
	}

	private BlockFace rotateBlockFace(BlockFace blockFace, Material powder) {
		if (powder.equals(Material.BLACK_CONCRETE_POWDER)) {
			switch (blockFace) {
				case SOUTH_WEST:
					return BlockFace.WEST;
				case WEST:
					return BlockFace.EAST;
				case EAST:
					return BlockFace.SOUTH_EAST;
				case SOUTH_EAST:
					return BlockFace.SOUTH_WEST;
			}
		} else {
			switch (blockFace) {
				case SOUTH_WEST:
					return BlockFace.NORTH_WEST;
				case NORTH_WEST:
					return BlockFace.NORTH_EAST;
				case NORTH_EAST:
					return BlockFace.SOUTH_EAST;
				case SOUTH_EAST:
					return BlockFace.SOUTH_WEST;
			}
		}

		return blockFace;
	}

	private void startLaser(Player player) {
		clearLamps();
		active = true;
		AtomicInteger cooldown = new AtomicInteger(5);
		AtomicInteger lifespan = new AtomicInteger(200);
		final BlockFace[] blockFace = {BlockFace.NORTH};
		final Location[] loc = {laserStart};

		taskId = Tasks.repeat(0, 1, () -> {
			if (active) {
				DotEffect.builder().player(player).location(loc[0]).speed(0.1).ticks(10).color(Color.RED).start();
				Block block = loc[0].getBlock();
				Material blockType = block.getType();
				double middle = loc[0].getX() - loc[0].getBlockX();
				if (middle == .5 && !blockType.equals(Material.AIR) && cooldown.get() == 0) {
					if (blockType.equals(Material.REDSTONE_LAMP)) {
						BlockData blockData = block.getBlockData();
						Lightable lightable = (Lightable) blockData;
						lightable.setLit(true);
						block.setBlockData(lightable);
					}
					endLaser();
					return;
				}

				Block below = block.getRelative(0, -1, 0);
				Material bannerType = below.getType();
				if (middle == .5 && MaterialTag.BANNERS.isTagged(bannerType) && cooldown.get() == 0) {
					Rotatable rotatable = (Rotatable) below.getBlockData();
					BlockFace newFace = getCardinal(rotatable.getRotation(), blockFace[0], below.getRelative(0, -2, 0).getType());
					if (newFace == null) {
						endLaser();
						return;
					}
					blockFace[0] = newFace;
					cooldown.set(5);
				}

				loc[0] = loc[0].clone().add(blockFace[0].getDirection().multiply(0.25));
				lifespan.getAndDecrement();

				if (cooldown.get() > 0)
					cooldown.getAndDecrement();

				if (lifespan.get() <= 0)
					endLaser();
			} else {
				endLaser();
			}
		});
	}

	private BlockFace getCardinal(BlockFace bannerFace, BlockFace laserFace, Material powder) {
		if (bannerFace.name().toLowerCase().contains(laserFace.name().toLowerCase())) {
			return null;
		}

		if (powder.equals(Material.BLACK_CONCRETE_POWDER)) {
			boolean bool = bannerFace.equals(BlockFace.WEST) || bannerFace.equals(BlockFace.EAST);
			if (laserFace.equals(BlockFace.NORTH)) {
				if (bool)
					return BlockFace.NORTH;
			} else {
				if (bool)
					return null;
			}
		}

		if (laserFace.equals(BlockFace.NORTH)) {
			if (bannerFace.equals(BlockFace.SOUTH_WEST))
				return BlockFace.WEST;
			else
				return BlockFace.EAST;

		} else if (laserFace.equals(BlockFace.SOUTH)) {
			if (bannerFace.equals(BlockFace.NORTH_WEST))
				return BlockFace.WEST;
			else
				return BlockFace.EAST;

		} else if (laserFace.equals(BlockFace.EAST)) {
			if (bannerFace.equals(BlockFace.SOUTH_WEST))
				return BlockFace.SOUTH;
			else
				return BlockFace.NORTH;

		} else if (laserFace.equals(BlockFace.WEST)) {
			if (bannerFace.equals(BlockFace.NORTH_EAST))
				return BlockFace.NORTH;
			else
				return BlockFace.SOUTH;
		}

		return null;
	}

	private void endLaser() {
		Tasks.cancel(taskId);
		active = false;
	}
}
