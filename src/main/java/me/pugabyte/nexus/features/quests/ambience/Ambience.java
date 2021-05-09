package me.pugabyte.nexus.features.quests.ambience;

import eden.annotations.Disabled;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.framework.features.Feature;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

@Disabled
@NoArgsConstructor
public class Ambience extends Feature implements Listener {
	@Getter
	private final Set<AmbienceUser> players = new HashSet<>();

	public void addPlayer(Player player) {
		AmbienceUser user = new AmbienceUser(player);

		// init events (setup cooldowns), so they don't all occur at once for the first time
		// TODO

		players.add(user);
	}

	public void removePlayer(Player player) {
		for (AmbienceUser user : new HashSet<>(players)) {
			if (user.getPlayer().getUniqueId().equals(player.getUniqueId()))
				user.getSoundPlayer().stopSounds();
			players.remove(user);
		}
	}

//	@EventHandler
//	public void onJoin(PlayerJoinEvent event){
//		addPlayer(event.getPlayer());
//	}
//
//	@EventHandler
//	public void onQuid(PlayerQuitEvent event){
//		removePlayer(event.getPlayer());
//	}

	@Override
	public void onStart() {

	}

	@Override
	public void onStop() {

	}
}
