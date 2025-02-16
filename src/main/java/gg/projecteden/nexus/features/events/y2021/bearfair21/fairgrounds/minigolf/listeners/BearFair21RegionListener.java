package gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.listeners;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2021.bearfair21.BearFair21;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.BearFair21MiniGolfHole;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BearFair21RegionListener implements Listener {
	Location hole14_returnLoc = new Location(BearFair21.getWorld(), 149.50, 122.0, -26.50, -90, 0);
	String hole14_return = BearFair21MiniGolfHole.FOURTEEN.getRegionId() + "_return";
	Location hole16_returnLoc = new Location(BearFair21.getWorld(), 141.5, 129.0, 4.5, -135, 0);
	String hole16_return = BearFair21MiniGolfHole.SIXTEEN.getRegionId() + "_return";

	public BearFair21RegionListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onRegionEnter(PlayerEnteredRegionEvent event) {
		Player player = event.getPlayer();
		if (BearFair21.isNotAtBearFair(player))
			return;

		if (event.getRegion().getId().equals(hole14_return))
			player.teleport(hole14_returnLoc);
		else if (event.getRegion().getId().equals(hole16_return))
			player.teleport(hole16_returnLoc);
	}
}
