package me.pugabyte.bncore.features.minigames.mechanics;

import com.boydti.fawe.object.schematic.Schematic;
import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.arenas.GoldRushArena;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.WorldEditUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GoldRush extends TeamlessMechanic {

	@Override
	public String getName() {
		return "Gold Rush";
	}

	@Override
	public String getDescription() {
		return "Mine all the blocks to the finish!";
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.GOLD_INGOT);
	}

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);
		Match match = event.getMatch();
		GoldRushArena goldRushArena = match.getArena();
		createMineStacks(goldRushArena.getMineStackHeight(), match.getAliveTeams().get(0).getSpawnpoints());
		for (Location loc : match.getAliveTeams().get(0).getSpawnpoints())
			loc.clone().subtract(0, 1, 0).getBlock().setType(Material.GLASS);

		Tasks.Countdown.builder()
				.duration(5 * 20)
				.onSecond(i -> match.broadcast("Starting in " + i + "..."))
				.onComplete(() -> {
					match.broadcast("Mine!");
					for (Location location : match.getAliveTeams().get(0).getSpawnpoints())
						location.clone().subtract(0, 1, 0).getBlock().breakNaturally();
					for (Minigamer minigamer : match.getMinigamers())
						minigamer.getPlayer().playSound(minigamer.getPlayer().getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
				})
				.start();
	}

	@Override
	public void onEnd(MatchEndEvent event) {
		super.onEnd(event);
		if (event.getMatch().isStarted()) {
			GoldRushArena goldRushArena = event.getMatch().getArena();
			for (Location location : event.getMatch().getAliveTeams().get(0).getSpawnpoints()) {
				removeMineStacks(goldRushArena.getMineStackHeight(), location);
			}
		}
	}

	public void createMineStacks(int mineStackHeight, List<Location> locations) {
		WorldEditUtils worldEditUtils = WEUtils;

		Map<Material, Double> pattern = new HashMap<Material, Double>() {{
			put(Material.COBBLESTONE, 10.0);
			put(Material.GOLD_ORE, 40.0);
			put(Material.DIRT, 20.0);
			put(Material.IRON_ORE, 20.0);
			put(Material.OAK_WOOD, 10.0);
		}};

		Vector p1 = worldEditUtils.toVector(locations.get(0).clone().subtract(0, 2, 0));
		Vector p2 = worldEditUtils.toVector(locations.get(0).clone().subtract(0, mineStackHeight, 0));
		Region region = new CuboidRegion(p1, p2);
		worldEditUtils.replace(region, Collections.singleton(Material.AIR), pattern);

		Schematic schematic = worldEditUtils.copy(locations.get(0).clone().subtract(0, 2, 0), locations.get(0).clone().subtract(0, mineStackHeight, 0));
		for (Location location : locations) {
			worldEditUtils.paste(schematic, worldEditUtils.toVector(location.clone().subtract(0, mineStackHeight, 0)));
		}
	}

	public void removeMineStacks(int mineStackHeight, Location loc) {
		WorldEditUtils worldEditUtils = WEUtils;
		Vector p1 = worldEditUtils.toVector(loc.clone().subtract(0, 2, 0));
		Vector p2 = worldEditUtils.toVector(loc.clone().subtract(0, mineStackHeight, 0));
		Region region = new CuboidRegion(p1, p2);
		worldEditUtils.fill(region, Material.AIR);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		event.setDropItems(false);
		if (event.getBlock().getType().equals(Material.IRON_ORE)) {
			trap(event.getBlock());
			event.getPlayer().sendMessage(Minigames.PREFIX + "You mined some fools gold! Next time, click it with the TNT to remove it!");
		}
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		if (event.getClickedBlock() == null) return;
		if (!event.getClickedBlock().getType().equals(Material.IRON_ORE)) return;
		if (!event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.TNT)) return;
		event.getClickedBlock().setType(Material.AIR);
	}

	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (minigamer.isPlaying(this)) event.setCancelled(true);
	}

	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		if (minigamer.getMatch().getArena().ownsRegion(event.getRegion().getId(), "win")) {
			minigamer.scored();
			minigamer.getMatch().end();
		}
	}

	public void trap(Block block) {
		Tasks.wait(1, () -> block.getRelative(BlockFace.UP).getLocation().clone().subtract(0, 1, 0).getBlock().setType(Material.COBWEB));
		Tasks.wait(2 * 20, () -> block.setType(Material.AIR));
	}

}
