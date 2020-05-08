package me.pugabyte.bncore.features.holidays.bearfair20.fairgrounds;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Archery implements Listener {
	WorldGuardUtils WGUtils = new WorldGuardUtils(BearFair20.world);
	WorldEditUtils WEUtils = new WorldEditUtils(BearFair20.world);
	private static String archeryRg = BearFair20.mainRg + "_archery";
	private static String targetsRg = archeryRg + "_targets";
	private static boolean archeryBool = false;
	private static int currentTargets = 0;

	public Archery() {
		BNCore.registerListener(this);
		targetTask();
	}

	private void targetTask() {
		List<Location> spawnLocs = getTargetLocs();
		Tasks.repeat(0, 20, () -> {
			if (archeryBool) {
				if (currentTargets < 10) {
					Location loc = Utils.getRandomElement(spawnLocs);
					if (canPlaceTarget(loc)) {
						placeTarget(loc);
						++currentTargets;
					}
				}
			}
		});
	}

	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase(archeryRg)) return;
		if (archeryBool) return;
		archeryBool = true;
	}

	@EventHandler
	public void onRegionExit(RegionLeftEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase(archeryRg)) return;
		if (!archeryBool) return;
		WorldGuardUtils WGUtils = new WorldGuardUtils(BearFair20.world);
		int size = WGUtils.getPlayersInRegion(archeryRg).size();
		if (size == 0) {
			archeryBool = false;
			clearTargets();
		}
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		if (!(projectile instanceof Arrow)) return;
		Block hitBlock = event.getHitBlock();
		if (hitBlock == null) return;
		if (!hitBlock.getType().equals(Material.WHITE_CONCRETE)) return;
		WorldGuardUtils WGUtils = new WorldGuardUtils(BearFair20.world);
		if (!WGUtils.getRegionNamesAt(hitBlock.getLocation()).contains(targetsRg)) return;
		if (!(projectile.getShooter() instanceof Player)) return;

		Player player = (Player) projectile.getShooter();
		projectile.remove();
		--currentTargets;
		removeTarget(hitBlock);
		player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.3F, 0.1F);
	}

	private List<Location> getTargetLocs() {
		List<Block> blocks = WEUtils.getBlocks((CuboidRegion) WGUtils.getRegion(targetsRg));
		List<Location> locs = new ArrayList<>();
		for (Block block : blocks) {
			Location loc = block.getLocation();
			if (canPlaceTarget(loc))
				locs.add(loc);
		}
		return locs;
	}

	private boolean canPlaceTarget(Location loc) {
		// GetBlocksInRadius --> NPE 391
		Block block = loc.getBlock();
		return block.getType().equals(Material.AIR)
				&& block.getRelative(BlockFace.UP).getType().equals(Material.AIR)
				&& block.getRelative(BlockFace.DOWN).getType().equals(Material.AIR)
				&& block.getRelative(BlockFace.WEST).getType().equals(Material.AIR)
				&& block.getRelative(BlockFace.EAST).getType().equals(Material.AIR)
				&& block.getRelative(BlockFace.SOUTH).getType().equals(Material.AIR)
				&& block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.DOWN).getType().equals(Material.AIR)
				&& block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP).getType().equals(Material.AIR)
				&& block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).getType().equals(Material.AIR)
				&& block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).getType().equals(Material.AIR)
				&& block.getRelative(BlockFace.NORTH).getType().equals(Material.AIR)
				&& block.getRelative(BlockFace.NORTH).getRelative(BlockFace.DOWN).getType().equals(Material.AIR)
				&& block.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP).getType().equals(Material.AIR)
				&& block.getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getType().equals(Material.AIR)
				&& block.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).getType().equals(Material.AIR);
	}

	private void placeTarget(Location location) {
		Block block = location.getBlock();
		int ran = Utils.randomInt(1, 14);
		Material coloredConcrete = ColorType.fromDurability(ran).getConcrete();

		block.setType(Material.WHITE_CONCRETE);
		block.getRelative(BlockFace.UP).setType(coloredConcrete);
		block.getRelative(BlockFace.DOWN).setType(coloredConcrete);
		block.getRelative(BlockFace.WEST).setType(coloredConcrete);
		block.getRelative(BlockFace.EAST).setType(coloredConcrete);

		Block south = block.getRelative(BlockFace.SOUTH);
		south.setType(Material.STONE_BUTTON);
		Directional data = (Directional) south.getBlockData();
		data.setFacing(BlockFace.SOUTH);
		south.setBlockData(data);
	}

	private void removeTarget(Block block) {
		Collection<Entity> entities = block.getWorld().getNearbyEntities(block.getLocation(), 2, 2, 2);
		for (Entity entity : entities) {
			if (entity.getType().equals(EntityType.ARROW)) {
				Arrow arrow = (Arrow) entity;
				if (arrow.isInBlock())
					entity.remove();
			}
		}

		block.getRelative(BlockFace.SOUTH).setType(Material.AIR);
		block.setType(Material.AIR);
		block.getRelative(BlockFace.UP).setType(Material.AIR);
		block.getRelative(BlockFace.DOWN).setType(Material.AIR);
		block.getRelative(BlockFace.WEST).setType(Material.AIR);
		block.getRelative(BlockFace.EAST).setType(Material.AIR);
	}

	private void clearTargets() {
		currentTargets = 0;
		List<Block> blocks = WEUtils.getBlocks((CuboidRegion) WGUtils.getRegion(targetsRg));
		for (Block block : blocks) {
			if (block.getType().equals(Material.WHITE_CONCRETE))
				removeTarget(block);
		}
	}
}
