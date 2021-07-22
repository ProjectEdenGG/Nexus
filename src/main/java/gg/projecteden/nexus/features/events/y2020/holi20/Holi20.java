package gg.projecteden.nexus.features.events.y2020.holi20;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;

public class Holi20 implements Listener {

	public Holi20() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onBucketFill(PlayerBucketFillEvent event) {
		Block eventBlock = event.getBlockClicked();
		Location loc = eventBlock.getLocation();
		WorldGuardUtils WGUtils = new WorldGuardUtils(loc);
		for (ProtectedRegion region : WGUtils.getRegionsAt(eventBlock.getLocation())) {
			if (region.getId().contains("quest_water")) {
				event.getPlayer().getInventory().remove(new ItemStack(Material.BUCKET));
//				event.getItemStack().setAmount(event.getItemStack().getAmount() - 1);
				PlayerUtils.giveItem(event.getPlayer(), new ItemStack(Material.WATER_BUCKET));
				event.setCancelled(true);
			}
		}
	}
}
