package me.pugabyte.bncore.features.minigames.mechanics;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;

public class InvertoInferno extends TeamlessMechanic {

	@Override
	public String getName() {
		return "Inverto Inferno";
	}

	@Override
	public String getDescription() {
		return "Put out the fire!";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.FLINT_AND_STEEL);
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@Override
	public void onStart(Match match) {
		super.onStart(match);
		new FireTask(match);
	}

	@Override
	public void onEnd(Match match) {
		super.onEnd(match);
		Region region = Minigames.getWorldGuardUtils().getRegion("invertoinferno");
		Minigames.getWorldEditUtils().replace(region, Material.FIRE, Material.AIR);
	}

	@Override
	public void onPlayerInteract(Minigamer minigamer, PlayerInteractEvent event) {
		super.onPlayerInteract(minigamer, event);
		if (event.isCancelled()) return;

		PlayerInventory playerInv = event.getPlayer().getInventory();
		int slot = playerInv.getHeldItemSlot();

		// Fill Water Bucket
		if (event.getItem() != null
				&& event.getItem().getType().equals(Material.BUCKET)
				&& Utils.isWater(event.getClickedBlock().getRelative(event.getBlockFace()).getType())) {
			playerInv.remove(event.getItem());
			playerInv.setItem(slot, new ItemStack(Material.WATER_BUCKET));
			event.setCancelled(true);
			return;

			// Water Extinguish
		} else if (event.getItem() != null && event.getItem().getType().equals(Material.WATER_BUCKET)) {
			Location location = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation();
			int points = 0;
			int radius = 3;
			for (int x = -radius; x <= radius; x++) {
				for (int z = -radius; z <= radius; z++) {
					for (int y = -radius; y <= radius; y++) {
						Block block = location.getBlock().getRelative(x, y, z);
						if (block.getType().equals(Material.FIRE)) {
							points++;
							block.setType(Material.AIR);
						}
					}
				}
			}
			playerInv.remove(event.getItem());
			playerInv.setItem(slot, new ItemStack(Material.BUCKET));
			minigamer.scored(points);

			// Hand Extinguish
		} else {
			Material fire = event.getClickedBlock().getRelative(event.getBlockFace()).getType();
			if (!fire.equals(Material.FIRE)) {
				event.setCancelled(true);
				return;
			}

			int random = Utils.randomInt(1, 100);
			if (random <= 5) {
				int current = event.getPlayer().getFireTicks();
				event.getPlayer().setFireTicks(current + 10 * 20);
			}

			minigamer.scored();

		}
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		Location location = event.getBlock().getLocation();

		for (ProtectedRegion region : Minigames.getWorldGuardUtils().getRegionsAt(location)) {
			if (region.getId().equalsIgnoreCase("invertoinferno")) {
				event.setCancelled(true);
				break;
			}
		}
	}

	public class FireTask {
		private Match match;
		private int taskId;
		private ProtectedRegion regionWG = Minigames.getWorldGuardUtils().getProtectedRegion("invertoinferno");
		private Region regionWE = Minigames.getWorldGuardUtils().convert(regionWG);
		private int percent = regionWG.volume() / 5;
		private int placedFire = 0;

		FireTask(Match match) {
			this.match = match;
			start();
		}

		void start() {
			taskId = match.getTasks().repeat(0, 5 * 20, () -> {
				if (match.isEnded())
					stop(taskId);

				EditSession editSession = Minigames.getWorldEditUtils().getEditSession();
				if (editSession.countBlocks(regionWE, Collections.singleton(new BaseBlock(Material.FIRE.getId()))) <= placedFire) {
					placedFire = 0;
					for (int i = 0; i < percent; i++) {
						Block block = Minigames.getWorldGuardUtils().getRandomBlock(regionWG);
						if (block.getType().isBurnable()) {
							Block above = block.getRelative(BlockFace.UP, 1);
							if (above.getType().equals(Material.AIR)) {
								above.setType(Material.FIRE);
								++placedFire;
							}
						}
					}
				}
				editSession.flushQueue();
			});
		}

		void stop(int taskId) {
			Utils.cancelTask(taskId);
		}
	}

}
