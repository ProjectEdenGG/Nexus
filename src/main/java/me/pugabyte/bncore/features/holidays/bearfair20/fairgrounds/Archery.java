package me.pugabyte.bncore.features.holidays.bearfair20.fairgrounds;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.models.bearfair.BearFairService;
import me.pugabyte.bncore.models.bearfair.BearFairUser;
import me.pugabyte.bncore.models.bearfair.BearFairUser.BFPointSource;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldEditUtils;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.*;

public class Archery implements Listener {
	WorldEditUtils WEUtils = new WorldEditUtils(BearFair20.world);
	private static String gameRg = BearFair20.BFRg + "_archery";
	private static String targetsRg = gameRg + "_targets";
	private static boolean archeryBool = false;
	private static int currentTargets = 0;
	private BFPointSource SOURCE = BFPointSource.ARCHERY;

	public Archery() {
		BNCore.registerListener(this);
		targetTask();
	}

	private void targetTask() {
		List<Location> spawnLocs = getTargetLocs();
		Tasks.repeat(0, 10, () -> {
			if (archeryBool) {
				if (currentTargets < 10) {
					Location loc = Utils.getRandomElement(spawnLocs);
					if (canPlaceTarget(loc, true)) {
						placeTarget(loc);
						++currentTargets;
					}
				}
			}
		});
	}

	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase(gameRg)) return;
		if (archeryBool) return;
		archeryBool = true;
	}

	@EventHandler
	public void onRegionExit(RegionLeftEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase(gameRg)) return;
		if (!archeryBool) return;
		int size = WGUtils.getPlayersInRegion(gameRg).size();
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
		if (!isInRegion(hitBlock, targetsRg)) return;
		if (!(projectile.getShooter() instanceof Player)) return;

		Player player = (Player) projectile.getShooter();
		projectile.remove();
		--currentTargets;
		removeTarget(hitBlock);
		player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.3F, 0.1F);

		if (givePoints) {
			BearFairUser user = new BearFairService().get(player);
			user.giveDailyPoints(1, SOURCE);
			new BearFairService().save(user);
		}
	}

	private List<Location> getTargetLocs() {
		List<Block> blocks = WEUtils.getBlocks((CuboidRegion) WGUtils.getRegion(targetsRg));
		List<Location> locs = new ArrayList<>();
		for (Block block : blocks) {
			Location loc = block.getLocation();
			if (canPlaceTarget(loc, false))
				locs.add(loc);
		}
		return locs;
	}


	private boolean canPlaceTarget(Location loc, boolean checkRadius) {
		if (loc == null)
			return false;
		if (checkRadius) {
			List<Block> nearbyBlocks = Utils.getBlocksInRadius(loc, 1);
			for (Block block : nearbyBlocks) {
				if (!block.getType().equals(Material.AIR))
					return false;
			}
			return true;
		} else
			return loc.getBlock().getType().equals(Material.AIR);
	}

	private void placeTarget(Location location) {
		Block block = location.getBlock();
		Set<Material> concretes = new HashSet<>(MaterialTag.CONCRETES.getValues());
		concretes.remove(Material.WHITE_CONCRETE);
		concretes.remove(Material.BLACK_CONCRETE);
		Material concrete = Utils.getRandomElement(concretes);

		block.setType(Material.WHITE_CONCRETE);
		block.getRelative(BlockFace.UP).setType(concrete);
		block.getRelative(BlockFace.DOWN).setType(concrete);
		block.getRelative(BlockFace.WEST).setType(concrete);
		block.getRelative(BlockFace.EAST).setType(concrete);

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
