package me.pugabyte.nexus.features.minigolf.listeners;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.minigolf.MiniGolf;
import me.pugabyte.nexus.features.minigolf.MiniGolfUtils;
import me.pugabyte.nexus.features.minigolf.models.GolfBall;
import me.pugabyte.nexus.features.minigolf.models.MiniGolfUser;
import me.pugabyte.nexus.features.minigolf.models.events.MiniGolfBallSpawnEvent;
import me.pugabyte.nexus.features.minigolf.models.events.MiniGolfUserPlaceBallEvent;
import me.pugabyte.nexus.utils.BlockUtils;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class InteractListener implements Listener {

	public InteractListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onPutt(PlayerInteractEvent event) {
		if (event.getHand() == null) return;
		if (!event.getHand().equals(EquipmentSlot.HAND)) return;

		MiniGolfUser user = MiniGolfUtils.getUser(event.getPlayer().getUniqueId());
		if (user == null)
			return;

		ItemStack item = event.getItem();
		if (ItemUtils.isNullOrAir(item)) {
			user.debug("item is null or air, returning");
			return;
		}

		Action action = event.getAction();
		Block block = event.getClickedBlock();

		if (item.getType().equals(Material.SNOWBALL)) {
			user.debug("placing golf ball...");

			if (user.getGolfBall() != null && user.getGolfBall().getSnowball() != null) {
				user.debug("you already have a ball placed");
				event.setCancelled(true);
				return;
			}

			if (BlockUtils.isNullOrAir(block)) {
				user.debug("placed on block is air or null");
				event.setCancelled(true);
				return;
			}

			GolfBall golfBall = new GolfBall(user.getUuid());

			// Verify region
			WorldGuardUtils WGUtils = new WorldGuardUtils(block);
			Set<ProtectedRegion> regions = WGUtils.getRegionsLikeAt(MiniGolf.holeRegionRegex, block.getLocation());
			ProtectedRegion region = regions.stream().findFirst().orElse(null);
			if (region == null) {
				user.debug("hole region not found");
				event.setCancelled(true);
				return;
			}

			golfBall.setHoleRegion(region.getId());

			// Place Event
			MiniGolfUserPlaceBallEvent placeBallEvent = new MiniGolfUserPlaceBallEvent(user, golfBall, Set.of(Material.GREEN_WOOL));
			if (!placeBallEvent.callEvent()) {
				user.debug("place ball event cancelled");
				event.setCancelled(true);
				return;
			}

			if (!placeBallEvent.canPlaceBall(block.getType())) {
				user.debug("incorrect starting position");
				event.setCancelled(true);
				return;
			}

			// Spawn Event
			MiniGolfBallSpawnEvent ballSpawnEvent = new MiniGolfBallSpawnEvent(golfBall, block.getLocation());
			if (!ballSpawnEvent.callEvent()) {
				user.debug("place spawn event cancelled");
				event.setCancelled(true);
				return;
			}

			item.setAmount(item.getAmount() - 1);
			ballSpawnEvent.spawnBall();
			MiniGolf.getGolfBalls().add(golfBall);
		}

		// TODO
	}
}
