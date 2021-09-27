package gg.projecteden.nexus.features.listeners;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class RegenRegions implements Listener {

	private int getInteger(String string) {
		int result;
		try {
			result = Integer.parseInt(string);
		} catch (Exception e) {
			result = -1;
		}
		return result;
	}

	private Material getMaterial(String string) {
		for (Material value : Material.values()) {
			if (value.toString().equalsIgnoreCase(string)) {
				return value;
			}
		}
		return null;
	}

	@EventHandler
	public void onBreakBlock(BlockBreakEvent event) {
		if (!(event.getPlayer().getGameMode().equals(GameMode.SURVIVAL))) return;
		Block eventBlock = event.getBlock();
		Location loc = eventBlock.getLocation();
		WorldGuardUtils worldguard = new WorldGuardUtils(loc);
		String key = "_regen_";

		for (ProtectedRegion region : worldguard.getRegionsAt(loc)) {
			String regionName = region.getId();
			if (regionName.contains(key)) {
				int ndx = regionName.indexOf('_');
				String variables = regionName.substring(ndx + key.length()-1);
				String[] varSplit = variables.split("_");

				String cooldownStr = varSplit[varSplit.length-1];
				int cooldown = getInteger(cooldownStr);
				String materialStr = variables;
				if(cooldown != -1)
					materialStr = variables.substring(0, variables.indexOf(cooldownStr));


				Material material = getMaterial(materialStr);

				if(material == null) {
					// throw error?
					Nexus.log("Unknown material to regen");
					return;
				}
			}
		}
	}

	public void regenBlock(Location location, Material material, int seconds) {
		Tasks.wait(seconds * 20, () -> {
			Block block = location.getBlock();
			block.setType(material);
		});
	}

}
