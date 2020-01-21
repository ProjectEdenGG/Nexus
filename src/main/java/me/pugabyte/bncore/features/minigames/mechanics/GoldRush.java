package me.pugabyte.bncore.features.minigames.mechanics;

import com.boydti.fawe.object.schematic.Schematic;
import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.arenas.GoldRushArena;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldEditUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
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
	public void onStart(Match match) {
		super.onStart(match);
		GoldRushArena goldRushArena = (GoldRushArena) match.getArena();
		createMineStacks(goldRushArena.getMineStackHeight(), match.getTeams().get(0).getSpawnpoints());
		for (Location loc : match.getTeams().get(0).getSpawnpoints())
			loc.clone().subtract(0, 1, 0).getBlock().setType(Material.GLASS);

		new Countdown(match, 5);
	}

	private class Countdown {
		private Match match;
		private int taskId;
		private int seconds;

		Countdown(Match match, int seconds) {
			this.match = match;
			this.seconds = seconds;
			start();
		}

		void start() {
			taskId = match.getTasks().repeat(0, 20, () -> {
				match.broadcast("Starting in " + seconds + "...");
				if (seconds < 1) {
					match.broadcast("Mine!");
					for (Location loc : match.getTeams().get(0).getSpawnpoints())
						loc.clone().subtract(0, 1, 0).getBlock().breakNaturally();
					for (Minigamer minigamer : match.getMinigamers())
						minigamer.getPlayer().playSound(minigamer.getPlayer().getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
					stop();
				}
				seconds--;
			});
		}

		void stop() {
			Utils.cancelTask(taskId);
		}
	}


	@Override
	public void onEnd(Match match) {
		super.onEnd(match);
		GoldRushArena goldRushArena = (GoldRushArena) match.getArena();
		for (Location loc : match.getTeams().get(0).getSpawnpoints()) {
			removeMineStacks(goldRushArena.getMineStackHeight(), loc);
		}
	}

	public void createMineStacks(int mineStackHeight, List<Location> locations) {
		WorldEditUtils worldEditUtils = Minigames.getWorldEditUtils();

		Map<Material, Double> pattern = new HashMap<Material, Double>() {{
			put(Material.COBBLESTONE, 10.0);
			put(Material.GOLD_ORE, 40.0);
			put(Material.DIRT, 20.0);
			put(Material.IRON_ORE, 20.0);
			put(Material.WOOD, 10.0);
		}};

		Vector p1 = worldEditUtils.toVector(locations.get(0).clone().subtract(0, 2, 0));
		Vector p2 = worldEditUtils.toVector(locations.get(0).clone().subtract(0, mineStackHeight, 0));
		Region region = new CuboidRegion(p1, p2);
		worldEditUtils.replace(region, Collections.singleton(Material.AIR), pattern);

		Schematic schemm = worldEditUtils.copy(locations.get(0).clone().subtract(0, 2, 0), locations.get(0).clone().subtract(0, mineStackHeight, 0));
		for (Location loc : locations) {
			worldEditUtils.paste(schemm, worldEditUtils.toVector(loc.clone().subtract(0, mineStackHeight, 0)));
		}
	}

	public void removeMineStacks(int mineStackHeight, Location loc) {
		WorldEditUtils worldEditUtils = Minigames.getWorldEditUtils();
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
			BNCore.log("Trapping @ " + event.getBlock().getLocation().getX() + " " + event.getBlock().getLocation().getZ());
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
	public void onRegionEnter(RegionEnteredEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		if (event.getRegion().getId().equalsIgnoreCase("goldrush_" + minigamer.getMatch().getArena().getName() + "_winningRegion")) {
			minigamer.scored();
			minigamer.getMatch().end();
		}
	}

	public void trap(Block block) {
		Utils.wait(1, () -> block.getRelative(BlockFace.UP).getLocation().clone().subtract(0, 1, 0).getBlock().setType(Material.WEB));
		Utils.wait(2 * 20, () -> block.setType(Material.AIR));
	}

}
