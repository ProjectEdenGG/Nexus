package me.pugabyte.nexus.features.events.y2020.bearfair20.fairgrounds;

import com.sk89q.worldedit.regions.CuboidRegion;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.y2020.bearfair20.BearFair20;
import me.pugabyte.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import me.pugabyte.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import me.pugabyte.nexus.models.bearfair20.BearFair20User;
import me.pugabyte.nexus.models.bearfair20.BearFair20User.BF20PointSource;
import me.pugabyte.nexus.models.bearfair20.BearFair20UserService;
import me.pugabyte.nexus.utils.BlockUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.Tasks;
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

import static me.pugabyte.nexus.features.events.y2020.bearfair20.BearFair20.getWEUtils;
import static me.pugabyte.nexus.features.events.y2020.bearfair20.BearFair20.getWGUtils;
import static me.pugabyte.nexus.features.events.y2020.bearfair20.BearFair20.giveDailyPoints;
import static me.pugabyte.nexus.features.events.y2020.bearfair20.BearFair20.isInRegion;

public class Archery implements Listener {
	private static String gameRg = BearFair20.getRegion() + "_archery";
	private static String targetsRg = gameRg + "_targets";
	private static boolean archeryBool = false;
	private static int currentTargets = 0;
	private BF20PointSource SOURCE = BF20PointSource.ARCHERY;

	public Archery() {
		Nexus.registerListener(this);
		targetTask();
	}

	private void targetTask() {
		List<Location> spawnLocs = getTargetLocs();
		Tasks.repeat(0, 10, () -> {
			if (archeryBool) {
				if (currentTargets < 10) {
					Location loc = RandomUtils.randomElement(spawnLocs);
					if (canPlaceTarget(loc, true)) {
						placeTarget(loc);
						++currentTargets;
					}
				}
			}
		});
	}

	@EventHandler
	public void onRegionEnter(PlayerEnteredRegionEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase(gameRg)) return;
		if (archeryBool) return;
		archeryBool = true;
	}

	@EventHandler
	public void onRegionExit(PlayerLeftRegionEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase(gameRg)) return;
		if (!archeryBool) return;
		int size = getWGUtils().getPlayersInRegion(gameRg).size();
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
		if (!(projectile.getShooter() instanceof Player player)) return;

		projectile.remove();
		--currentTargets;
		removeTarget(hitBlock);
		player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.3F, 0.1F);

		if (giveDailyPoints) {
			BearFair20User user = new BearFair20UserService().get(player);
			user.giveDailyPoints(SOURCE);
			new BearFair20UserService().save(user);
		}
	}

	private List<Location> getTargetLocs() {
		List<Block> blocks = BearFair20.getWEUtils().getBlocks((CuboidRegion) getWGUtils().getRegion(targetsRg));
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
			List<Block> nearbyBlocks = BlockUtils.getBlocksInRadius(loc, 1);
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
		Material concrete = RandomUtils.randomElement(concretes);

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
		List<Block> blocks = getWEUtils().getBlocks((CuboidRegion) getWGUtils().getRegion(targetsRg));
		for (Block block : blocks) {
			if (block.getType().equals(Material.WHITE_CONCRETE))
				removeTarget(block);
		}
	}
}
