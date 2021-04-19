package me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.listeners;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolf;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RegionListener implements Listener {
	Location hole14_returnLoc = new Location(BearFair21.getWorld(), 149.50, 122.0, -26.50, -90, 0);
	String hole14_return = MiniGolf.getRegionHole() + "14_return";
	Location hole16_returnLoc = new Location(BearFair21.getWorld(), 141.5, 129.0, 4.5, -135, 0);
	String hole16_return = MiniGolf.getRegionHole() + "16_return";

	public RegionListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		if (!BearFair21.isAtBearFair(player))
			return;

		if (event.getRegion().getId().equals(hole14_return))
			player.teleport(hole14_returnLoc);
		else if (event.getRegion().getId().equals(hole16_return))
			player.teleport(hole16_returnLoc);
	}
}
