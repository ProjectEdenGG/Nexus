package me.pugabyte.bncore.features.holidays.bearfair20.fairgrounds;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.features.particles.effects.DotEffect;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldEditUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;
import static org.bukkit.block.BlockFace.*;

// TODO: Make an objective, tell players about it
// TODO: make a sound when the laser stops
// TODO: make a sound when the laser reflects?
public class Reflection implements Listener {

	WorldEditUtils WEUtils = new WorldEditUtils(BearFair20.world);
	private String gameRg = BearFair20.mainRg + "_reflection";
	private String powderRg = gameRg + "_powder";
	private boolean active = false;
	private int taskId;
	private Location laserStart;
	private List<Location> lampLocList = new ArrayList<>();
	private Location center = new Location(BearFair20.world, -950, 137, -1689);
	private List<BlockFace> directions = Arrays.asList(NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST);

	public Reflection() {
		BNCore.registerListener(this);
		setLamps();
	}

	private void setLamps() {
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

	private void randomizeBanners() {
		ProtectedRegion region = WGUtils.getProtectedRegion(powderRg);
		List<Block> blocks = WEUtils.getBlocks(region);
		for (Block block : blocks) {
			if (!MaterialTag.CONCRETE_POWDERS.isTagged(block.getType())) continue;

			Block banner = block.getRelative(0, 2, 0);
			BlockData blockData = banner.getBlockData();
			if (!(blockData instanceof Rotatable)) continue;
			Rotatable rotatable = (Rotatable) blockData;

			if (block.getType().equals(Material.CYAN_CONCRETE_POWDER)) {
				rotatable.setRotation(Utils.getRandomElement(directions));
				banner.setBlockData(rotatable);
			}
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
			rotateBanner(banner);
		} else {
			if (!active) {
				Location start = Utils.getCenteredLocation(powder.getRelative(0, 3, 0).getLocation());
				start.setY(start.getY() + 0.25);
				laserStart = start;
				startLaser(event.getPlayer());
			}
		}
	}

	private void rotateBanner(Block banner) {
		BlockData blockData = banner.getBlockData();
		Rotatable rotatable = (Rotatable) blockData;
		rotatable.setRotation(rotateBlockFace(rotatable.getRotation()));
		banner.setBlockData(rotatable);
	}

	private BlockFace rotateBlockFace(BlockFace blockFace) {
		int ndx = directions.indexOf(blockFace) + 1;
		if (ndx == directions.size())
			ndx = 0;
		return directions.get(ndx);
	}

	private void startLaser(Player player) {
		active = true;
		clearLamps();
		AtomicInteger cooldown = new AtomicInteger(5);
		AtomicInteger lifespan = new AtomicInteger(500);
		final BlockFace[] blockFace = {NORTH};
		final Location[] loc = {laserStart.clone()};

		taskId = Tasks.repeat(0, 1, () -> {
			if (active) {
				DotEffect.builder().player(player).location(loc[0].clone()).speed(0.1).ticks(10).color(Color.RED).start();
				Block block = loc[0].getBlock();
				Material blockType = block.getType();

				double middle = loc[0].getX() - loc[0].getBlockX();
				if (middle == 0.5 && !blockType.equals(Material.AIR) && cooldown.get() == 0) {
					if (blockType.equals(Material.REDSTONE_LAMP)) {
						BlockData blockData = block.getBlockData();
						Lightable lightable = (Lightable) blockData;
						lightable.setLit(true);
						block.setBlockData(lightable);
						win();
					}
					endLaser();
					return;
				}

				Block below = block.getRelative(0, -1, 0);
				Material bannerType = below.getType();
				if (middle == 0.5 && MaterialTag.BANNERS.isTagged(bannerType) && cooldown.get() == 0) {
					loc[0] = Utils.getCenteredLocation(loc[0]);
					loc[0].setY(loc[0].getY() + 0.25);
					Rotatable rotatable = (Rotatable) below.getBlockData();
					BlockFace newFace = getReflection(rotatable.getRotation(), blockFace[0]);
					if (newFace == null) {
						endLaser();
						return;
					}
					blockFace[0] = newFace;
					cooldown.set(5);
				}

				loc[0] = loc[0].clone().add(blockFace[0].getDirection().multiply(0.25));
				lifespan.getAndDecrement();

				if (cooldown.get() > 0) {
					cooldown.getAndDecrement();
				}

				if (lifespan.get() <= 0) {
					endLaser();
				}
			} else {
				endLaser();
			}
		});
	}

	private BlockFace getReflection(BlockFace bannerFace, BlockFace laserFace) {
		if (bannerFace.name().toLowerCase().contains(laserFace.name().toLowerCase())) {
			return null;
		}

		if (laserFace.getOppositeFace().equals(bannerFace))
			return laserFace.getOppositeFace();

		if (laserFace.equals(NORTH)) {
			if (bannerFace.equals(WEST) || bannerFace.equals(EAST))
				return laserFace;

			if (bannerFace.equals(SOUTH_WEST))
				return WEST;
			else
				return EAST;

		} else if (laserFace.equals(SOUTH)) {
			if (bannerFace.equals(WEST) || bannerFace.equals(EAST))
				return laserFace;

			if (bannerFace.equals(NORTH_WEST))
				return WEST;
			else
				return EAST;

		} else if (laserFace.equals(EAST)) {
			if (bannerFace.equals(SOUTH) || bannerFace.equals(NORTH))
				return laserFace;

			if (bannerFace.equals(SOUTH_WEST))
				return SOUTH;
			else
				return NORTH;

		} else if (laserFace.equals(WEST)) {
			if (bannerFace.equals(SOUTH) || bannerFace.equals(NORTH))
				return laserFace;

			if (bannerFace.equals(NORTH_EAST))
				return NORTH;
			else
				return SOUTH;
		}

		return laserFace;
	}

	private void endLaser() {
		Tasks.cancel(taskId);
		Tasks.wait(Time.SECOND.x(2), () -> active = false);
	}

	private void win() {
		randomizeBanners();
		BearFair20.world.playSound(center, Sound.BLOCK_NOTE_BLOCK_BELL, 1F, 1F);
		Collection<Player> players = WGUtils.getPlayersInRegion(gameRg);
		for (Player player : players) {
			BearFair20.givePoints(player, 1);
		}
	}
}
