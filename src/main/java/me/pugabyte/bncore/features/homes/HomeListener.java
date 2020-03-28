package me.pugabyte.bncore.features.homes;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.warps.Warps;
import me.pugabyte.bncore.models.home.Home;
import me.pugabyte.bncore.models.home.HomeOwner;
import me.pugabyte.bncore.models.home.HomeService;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Arrays;
import java.util.Optional;

public class HomeListener implements Listener {

	public HomeListener() {
		BNCore.registerListener(this);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		WorldGroup group = WorldGroup.get(event.getPlayer());
		if (!Arrays.asList(WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK).contains(group))
			return;

		HomeOwner homeOwner = new HomeService().get(event.getPlayer().getUniqueId());
		Optional<Home> main = homeOwner.getHome("home");
		Optional<Home> first = homeOwner.getHomes().stream().findFirst();

		if (main.isPresent())
			event.setRespawnLocation(main.get().getLocation());
		else if (first.isPresent())
			event.setRespawnLocation(first.get().getLocation());
		else
			event.setRespawnLocation(Warps.getSpawn());
	}
}
