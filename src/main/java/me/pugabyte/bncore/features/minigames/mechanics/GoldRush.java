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

public class GoldRush extends TeamlessMechanic {

	Match match;
	int mineStackHeight = 50;

	@Override
	public String getName() {
		return "Gold Rush";
	}

	@Override
	public String getDescription() {
		return "Mine all the blocks to the finish!";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.GOLD_INGOT);
	}

	int taskID;
	int seconds = 5;

	@Override
	public void onStart(Match match){
		this.match = match;
		GoldRushArena goldRushArena = (GoldRushArena) match.getArena();
		mineStackHeight = goldRushArena.getMineStackHeight();
		createMineStacks(match.getTeams().get(0).getSpawnpoints());
		for(Location loc : match.getTeams().get(0).getSpawnpoints()){
			loc.clone().subtract(0, 1, 0).getBlock().setType(Material.GLASS);
		}
		taskID = match.getTasks().repeat(3 *20, 20, ()->{
			match.broadcast("Starting in " + seconds + "...");
			if(seconds < 1){
				cancelTask();
				match.broadcast("Mine!");
				for(Location loc : match.getTeams().get(0).getSpawnpoints()){
					loc.clone().subtract(0, 1, 0).getBlock().breakNaturally();
				}
				for (Minigamer minigamer : match.getMinigamers()) {
					minigamer.getPlayer().playSound(minigamer.getPlayer().getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
					minigamer.getPlayer().setGameMode(GameMode.SURVIVAL);
				}
			}
			seconds--;
		});
	}

	@Override
	public void onEnd(Match match) {
		for (Location loc : match.getTeams().get(0).getSpawnpoints()) {
			removeMineStacks(loc);
		}
	}

	public void createMineStacks(List<Location> locations){
		WorldEditUtils worldEditUtils = Minigames.getWorldEditUtils();

		Map<Material, Double> pattern = new HashMap<Material, Double>() {{
			put(Material.COBBLESTONE, 10.0);
			put(Material.GOLD_ORE, 50.0);
			put(Material.DIRT, 20.0);
			put(Material.IRON_ORE, 10.0);
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

	public void removeMineStacks(Location loc){
		WorldEditUtils worldEditUtils = Minigames.getWorldEditUtils();
		Vector p1 = worldEditUtils.toVector(loc.clone().subtract(0, 2, 0));
		Vector p2 = worldEditUtils.toVector(loc.clone().subtract(0, mineStackHeight, 0));
		Region region = new CuboidRegion(p1, p2);
		worldEditUtils.fill(region, Material.AIR);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		event.setDropItems(false);
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		BNCore.log(event.getBlock().getType().name());
		if (event.getBlock().getType().equals(Material.IRON_ORE)) {
			BNCore.log("Trapping @ " + event.getBlock().getLocation().getX() + " " + event.getBlock().getLocation().getZ());
			trap(event.getBlock());
			event.getPlayer().sendMessage(Minigames.PREFIX + "You mined some fools gold! Next time, click it with the TNT to remove it!");
		}
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if(!minigamer.isPlaying(this)) return;
		if(event.getClickedBlock() == null) return;
		if(!event.getClickedBlock().getType().equals(Material.IRON_ORE)) return;
		if(!event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.TNT)) return;
		event.getClickedBlock().setType(Material.AIR);
	}

	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event){
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if(!minigamer.isPlaying(this)) return;
		if (event.getRegion().getId().equalsIgnoreCase("goldrush_" + match.getArena().getName() + "_winningRegion")) {
			minigamer.scored();
			match.end();
		}
	}

	public void trap(Block block){
		Utils.wait(1, () -> block.getRelative(BlockFace.UP).getLocation().clone().subtract(0, 1, 0).getBlock().setType(Material.WEB));
		Utils.wait(2 * 20, () -> block.setType(Material.AIR));
	}

	void cancelTask(){
		Utils.cancelTask(taskID);
	}

}
