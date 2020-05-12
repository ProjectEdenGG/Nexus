package me.pugabyte.bncore.features.holidays.bearfair20.quests;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Beehive implements Listener {
	private String enterRg = "bearfair2020_beehive_enter";
	private String exitRg = "bearfair2020_beehive_exit";
	private String allowedMsg = "TODO: Allowed message here";
	private String deniedMsg = "TODO: Denied message here";
	private Location enterLoc = new Location(BearFair20.world, -1084, 135, -1548, 228, 20);
	private Location exitLoc = new Location(BearFair20.world, -1088, 136, -1548, 40, 0);
	private Material key = Material.HONEY_BOTTLE;

	public Beehive() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event) {
		String id = event.getRegion().getId();
		Player player = event.getPlayer();

		if (id.equalsIgnoreCase(enterRg)) {
			if (player.getInventory().contains(key)) {
				player.teleport(enterLoc);
				player.sendMessage(allowedMsg);
			} else {
				player.sendMessage(deniedMsg);
			}
		} else if (id.equalsIgnoreCase(exitRg)) {
			player.teleport(exitLoc);
		}

	}
}
