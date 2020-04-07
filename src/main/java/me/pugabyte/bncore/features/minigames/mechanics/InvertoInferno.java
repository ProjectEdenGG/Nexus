package me.pugabyte.bncore.features.minigames.mechanics;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;
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
import java.util.List;

public final class InvertoInferno extends TeamlessMechanic {

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
	public void onStart(MatchStartEvent event) {
		super.onStart(event);

		new FireTask(event.getMatch());
		for (Minigamer minigamer : event.getMatch().getMinigamers())
			minigamer.tell("Keep the flames back until the water bombers arrive!");
	}

	@Override
	public void onEnd(MatchEndEvent event) {
		super.onEnd(event);
		Region region = WGUtils.getRegion("invertoinferno_fire");

		WEUtils.replace(region, Material.FIRE, Material.AIR);
	}

	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		if (!player.getWorld().equals(Minigames.getWorld())) return;

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

		if (!player.getWorld().equals(Minigames.getWorld())) return;

		Minigamer minigamer = PlayerManager.get((Player) source);
		if (!minigamer.isPlaying(this)) return;

		PlayerInventory playerInv = player.getInventory();
		int slot = playerInv.getHeldItemSlot();

		if (Utils.isWater(player.getTargetBlock(null, 10).getType())) {
			projectile.remove();
			Tasks.wait(1, () -> giveWaterBottle(player));
			return;
		}

		playerInv.remove(playerInv.getItem(slot));

		Tasks.wait(10 * 20, () -> playerInv.setItem(slot, new ItemStack(Material.GLASS_BOTTLE)));
	}

	// Radius Extinguish
	@EventHandler
	public void onWaterPotionSplash(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		ProjectileSource source = projectile.getShooter();
		if (!(source instanceof Player)) return;

		Player player = (Player) projectile.getShooter();
		if (!player.getWorld().equals(Minigames.getWorld())) return;

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

		List<Entity> nearbyEntities = projectile.getNearbyEntities(radius, radius, radius);
		for (Entity entity : nearbyEntities)
			entity.setFireTicks(0);

		minigamer.scored(points);
	}

	@EventHandler
	public void onGlassBottleFill(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!player.getWorld().equals(Minigames.getWorld())) return;

		Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isPlaying(this)) return;

		ItemStack eventItem = event.getItem();
		if (eventItem == null) return;
		if (eventItem.getType().equals(Material.SPLASH_POTION)) return;

		if (eventItem.getType().equals(Material.GLASS_BOTTLE) || eventItem.getType().equals(Material.POTION))
			event.setCancelled(true);

		if (event.getClickedBlock() == null || event.getBlockFace() == null) {
			if (Utils.isWater(player.getTargetBlock(null, 10).getType()))
				if (player.getLocation().distance(player.getTargetBlock(null, 10).getLocation()) > 4)
					minigamer.tell("You need to be closer to do that.");
				else
					giveWaterBottle(player);
			return;
		}

		// Water Bottle ->  Water Splash
		if (eventItem.getType().equals(Material.POTION)) {
			giveWaterBottle(player);
			return;
		}

		// via Water Or Cauldron: Glass Bottle -> Water Splash
		Block clickedBlock = event.getClickedBlock();
		if (Utils.isWater(clickedBlock.getRelative(event.getBlockFace()).getType())
				|| clickedBlock.getType().equals(Material.CAULDRON))
			giveWaterBottle(player);
	}

	private void giveWaterBottle(Player player) {
		ItemStack waterPotion = new ItemStack(Material.SPLASH_POTION);
		PotionMeta meta = (PotionMeta) waterPotion.getItemMeta();
		meta.setBasePotionData(new PotionData(PotionType.WATER));
		waterPotion.setItemMeta(meta);
		PlayerInventory playerInv = player.getInventory();
		int slot = playerInv.getHeldItemSlot();

		playerInv.remove(playerInv.getItem(slot));
		playerInv.setItem(slot, waterPotion);
	}

	// Hand Extinguish
	@Override
	public void onPlayerInteract(Minigamer minigamer, PlayerInteractEvent event) {
		super.onPlayerInteract(minigamer, event);
		if (event.isCancelled()) return;

		Material clickedMaterial = event.getClickedBlock().getRelative(event.getBlockFace()).getType();
		if (clickedMaterial.equals(Material.FIRE)) {
			minigamer.scored();

			int random = Utils.randomInt(1, 100);
			if (random <= 5) {
				int current = event.getPlayer().getFireTicks();
				event.getPlayer().setFireTicks(current + 10 * 20);
			}
		}

	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (!event.getPlayer().getWorld().equals(Minigames.getWorld())) return;

		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		Block eventBlock = event.getBlock();

		for (ProtectedRegion region : WGUtils.getRegionsAt(eventBlock.getLocation())) {
			if (region.getId().equalsIgnoreCase("invertoinferno") && !eventBlock.getType().equals(Material.FIRE)) {
				event.setCancelled(true);
				break;
			}
		}
	}

	@EventHandler
	public void onFireSpread(BlockSpreadEvent event) {
		if (!event.getBlock().getWorld().equals(Minigames.getWorld())) return;

		Location location = event.getBlock().getLocation();

		for (ProtectedRegion region : WGUtils.getRegionsAt(location)) {
			if (region.getId().equalsIgnoreCase("invertoinferno_fire")) {
				if (event.getSource().getType().equals(Material.FIRE)) {
					int chance = Utils.randomInt(1, 3);
					if (chance != 1)
						event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		if (!event.getBlock().getWorld().equals(Minigames.getWorld())) return;

		Location location = event.getBlock().getLocation();

		for (ProtectedRegion region : WGUtils.getRegionsAt(location)) {
			if (region.getId().equalsIgnoreCase("invertoinferno_fire")) {
				event.setCancelled(true);
				break;
			}
		}
	}

	public class FireTask {
		private Match match;
		private int taskId;
		private ProtectedRegion regionWG = WGUtils.getProtectedRegion("invertoinferno_fire");
		private Region regionWE = WGUtils.convert(regionWG);
		private int percent = regionWG.volume() / 25;
		private int placedFire = 0;

		FireTask(Match match) {
			this.match = match;
			start();
		}

		void start() {
			taskId = match.getTasks().repeat(0, Time.SECOND.x(5), () -> {
				if (match.isEnded())
					stop(taskId);

				EditSession editSession = WEUtils.getEditSession();
				if (editSession.countBlocks(regionWE, Collections.singleton(new BaseBlock(BlockTypes.FIRE))) <= placedFire) {
					placedFire = 0;
					for (int i = 0; i < percent; i++) {
						Block block = WGUtils.getRandomBlock(regionWG);
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
			Tasks.cancel(taskId);
		}
	}

}
