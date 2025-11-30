package gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.minigolf.MiniGolf;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.models.minigolf.MiniGolfUserService;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class Pugmas25MiniGolf implements Listener {
	public static final Location MINIGOLF_ANIMATION_LOCATION = Pugmas25.get().location(-712, 67, -2883);

	public Pugmas25MiniGolf() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(PlayerEnteredRegionEvent event) {
		var player = event.getPlayer();
		var regions = getMinigolfRegions(player);

		if (regions.isEmpty())
			return;

		if (!new Pugmas25UserService().get(player).isStartedMiniGolf())
			return;

		MiniGolf.join(new MiniGolfUserService().get(player));
	}

	@EventHandler
	public void on(PlayerLeftRegionEvent event) {
		var player = event.getPlayer();
		var regions = getMinigolfRegions(player);

		if (!regions.isEmpty())
			return;

		MiniGolf.quit(new MiniGolfUserService().get(player));
	}

	private static @NotNull Set<ProtectedRegion> getMinigolfRegions(Player player) {
		var worldguard = Pugmas25.get().worldguard();
		return worldguard.getRegionsLikeAt("pugmas25_minigolf_course.*", player.getLocation());
	}

}
