package me.pugabyte.bncore.features.homes;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.Koda;
import me.pugabyte.bncore.features.warps.Warps;
import me.pugabyte.bncore.models.home.Home;
import me.pugabyte.bncore.models.home.HomeOwner;
import me.pugabyte.bncore.models.home.HomeService;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class HomeListener implements Listener {
	Map<UUID, Location> deathLocations = new HashMap<>();

	public HomeListener() {
		BNCore.registerListener(this);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		WorldGroup group = WorldGroup.get(event.getPlayer());
		if (!Arrays.asList(WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK).contains(group))
			return;

		HomeOwner homeOwner = new HomeService().get(event.getPlayer().getUniqueId());
		Optional<Home> respawn = homeOwner.getHomes().stream().filter(Home::isRespawn).findFirst();
		Optional<Home> main = homeOwner.getHome("home");
		Optional<Home> first = homeOwner.getHomes().stream().findFirst();

		if (respawn.isPresent())
			event.setRespawnLocation(respawn.get().getLocation());
		else if (main.isPresent())
			event.setRespawnLocation(main.get().getLocation());
		else if (first.isPresent())
			event.setRespawnLocation(first.get().getLocation());
		else
			event.setRespawnLocation(Warps.getSpawn());
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if (!WorldGroup.get(player).equals(WorldGroup.SURVIVAL)) return;

		HomeService service = new HomeService();
		HomeOwner homeOwner = service.get(player);
		if (homeOwner.getHomes().size() != 0) return;
		if (homeOwner.isUsedDeathHome()) return;

		Location deathLoc = player.getLocation();
		deathLocations.putIfAbsent(player.getUniqueId(), deathLoc);
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if (!WorldGroup.get(player).equals(WorldGroup.SURVIVAL)) return;

		UUID uuid = player.getUniqueId();
		if (!deathLocations.containsKey(uuid)) return;

		Location deathLoc = deathLocations.get(uuid);
		String homeName = "death";
		HomeService service = new HomeService();
		HomeOwner homeOwner = service.get(player);

		homeOwner.add(Home.builder()
				.uuid(uuid)
				.name(homeName)
				.location(deathLoc)
				.build());

		Koda.dm(player, "Uh oh! You died without a home set! On Bear Nation we have a system called Homes that allow " +
				"you to save your location. I saved a home for you where you died, and you can teleport back to it with " +
				"&c/home " + homeName + " &f- but be careful! I can only do this once for you! Use &c/sethome [name] &fand &c/homes edit " +
				"&fto manage your homes in the future. Good luck!");

		homeOwner.setUsedDeathHome(true);
		deathLocations.remove(uuid);
		service.save(homeOwner);
	}
}
