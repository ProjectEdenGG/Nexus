package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class GoldRush extends TeamlessMechanic {
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
		for(Location loc : match.getTeams().get(0).getSpawnpoints()){
			loc.subtract(0, 1, 0).getBlock().setType(Material.GLASS);
		}
		taskID = match.getTasks().repeat(3 *20, 20, ()->{
			match.broadcast("Starting in " + seconds + "...");
			if(seconds < 1){
				cancelTask();
				for(Location loc : match.getTeams().get(0).getSpawnpoints()){
					Block block = loc.subtract(0, 1, 0).getBlock();
					block.setType(Material.AIR, true);
				}
			}
			seconds--;
		});

	}

	void cancelTask(){
		Utils.cancelTask(taskID);
	}

}
