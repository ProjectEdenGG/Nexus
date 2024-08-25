package gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.minigolf.models.MiniGolfUser;
import gg.projecteden.nexus.features.minigolf.models.events.MiniGolfUserJoinEvent;
import gg.projecteden.nexus.features.minigolf.models.events.MiniGolfUserQuitEvent;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

// TODO
public class Pugmas24Minigolf implements Listener {
	public static final Location minigolfAnimationLoc = Pugmas24.get().location(-712, 67, -2883);
	private final Pugmas24 PUGMAS = Pugmas24.get();
	private final WorldGuardUtils worldguard = PUGMAS.worldguard();

	public static Map<MiniGolfUser, String> players = new HashMap<>();

	public Pugmas24Minigolf() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(MiniGolfUserJoinEvent event) {
		MiniGolfUser user = event.getUser();
		Player player = getPlayer(user);
		if (player == null)
			return;

		if (!event.getCourseRegion().getId().startsWith("pugmas24"))
			return;

		Dev.WAKKA.send("playing pugmas24 minigolf");
		players.put(user, null);
	}

	@EventHandler
	public void on(MiniGolfUserQuitEvent event) {
		MiniGolfUser user = event.getUser();
		Player player = getPlayer(user);
		if (player == null)
			return;

		if (!players.containsKey(user))
			return;

		Dev.WAKKA.send("quit pugmas24 minigolf");
		players.remove(user);
	}

//	@EventHandler
//	public void on(MiniGolfBallDeathEvent event){
//
//	}
//
//	@EventHandler
//	public void on(MiniGolfBallSpawnEvent event){
//
//	}

	private Player getPlayer(MiniGolfUser user) {
		Player player = user.getPlayer();
		if (player == null || !player.isOnline())
			return null;

		return player;
	}


}
