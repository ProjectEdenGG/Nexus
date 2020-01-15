package me.pugabyte.bncore.features.minigames.mechanics;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.BlockIterator;

import java.util.Collections;

// TODO: change water place event to splash potion throw

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

	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		if (!player.getWorld().equals(Minigames.getGameworld())) return;

		Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isPlaying(this)) return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onWaterPotionThrow(ProjectileLaunchEvent event) {
		Projectile projectile = event.getEntity();
		ProjectileSource source = projectile.getShooter();
		if (!(source instanceof Player)) return;
		Player player = (Player) projectile.getShooter();

		if (!player.getWorld().equals(Minigames.getGameworld())) return;

		Minigamer minigamer = PlayerManager.get((Player) source);
		if (!minigamer.isPlaying(this)) return;

		PlayerInventory playerInv = player.getInventory();
		int slot = playerInv.getHeldItemSlot();
		playerInv.remove(playerInv.getItem(slot));

		Utils.wait(1, () -> playerInv.setItem(slot, new ItemStack(Material.GLASS_BOTTLE)));
	}

	// Radius Extinguish
	@EventHandler
	public void onWaterPotionSplash(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		ProjectileSource source = projectile.getShooter();
		if (!(source instanceof Player)) return;

		Player player = (Player) projectile.getShooter();
		if (!player.getWorld().equals(Minigames.getGameworld())) return;

		Minigamer minigamer = PlayerManager.get((Player) source);
		if (!minigamer.isPlaying(this)) return;

		// cancel event
		projectile.remove();

		BlockIterator blockIter = new BlockIterator(projectile.getWorld(), projectile.getLocation().toVector(), projectile.getVelocity().normalize(), 0, 4);
		Block blockHit = null;

		while (blockIter.hasNext()) {
			blockHit = blockIter.next();
			if (blockHit.getType() != Material.AIR) break;
		}

		if (blockHit == null) return;

		Location location = blockHit.getLocation();

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
		minigamer.scored(points);
	}

	@EventHandler
	public void onGlassBottleFill(PlayerInteractEvent event) {
		if (!event.getPlayer().getWorld().equals(Minigames.getGameworld())) return;

		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		ItemStack eventItem = event.getItem();
		if (eventItem == null) return;
		if (eventItem.getType().equals(Material.SPLASH_POTION)) return;

		if (eventItem.getType().equals(Material.GLASS_BOTTLE) || eventItem.getType().equals(Material.POTION))
			event.setCancelled(true);

		if (event.getClickedBlock() == null || event.getBlockFace() == null) {
			if (Utils.isWater(minigamer.getPlayer().getTargetBlock(null, 10).getType()))
				minigamer.tell("You need to be closer to do that.");
			return;
		}

		ItemStack waterPotion = new ItemStack(Material.SPLASH_POTION);
		PotionMeta meta = (PotionMeta) waterPotion.getItemMeta();
		meta.setBasePotionData(new PotionData(PotionType.WATER));
		waterPotion.setItemMeta(meta);
		PlayerInventory playerInv = event.getPlayer().getInventory();
		int slot = playerInv.getHeldItemSlot();

		if (eventItem.getType().equals(Material.GLASS_BOTTLE)
				&& Utils.isWater(event.getClickedBlock().getRelative(event.getBlockFace()).getType())) {
			playerInv.remove(playerInv.getItem(slot));
			playerInv.setItem(slot, waterPotion);
		}

		// if you have a water bottle in your hand, set it to a splash potion
		if (eventItem.getType().equals(Material.POTION)) {
			playerInv.remove(playerInv.getItem(slot));
			playerInv.setItem(slot, waterPotion);
		}
	}

	// Hand Extinguish
	@Override
	public void onPlayerInteract(Minigamer minigamer, PlayerInteractEvent event) {
		super.onPlayerInteract(minigamer, event);
		if (event.isCancelled()) return;

		ItemStack eventItem = event.getItem();

		Material clickedMaterial = event.getClickedBlock().getRelative(event.getBlockFace()).getType();
		if (clickedMaterial.equals(Material.FIRE) && Utils.isNullOrAir(eventItem)) {
			minigamer.scored();

			int random = Utils.randomInt(1, 100);
			if (random <= 5) {
				int current = event.getPlayer().getFireTicks();
				event.getPlayer().setFireTicks(current + 10 * 20);
			}
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
