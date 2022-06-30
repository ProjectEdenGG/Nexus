package gg.projecteden.nexus.features.ambience.old;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.framework.features.Feature;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

@Disabled
@NoArgsConstructor
public class AmbienceOld extends Feature implements Listener {
	@Getter
	private final Set<AmbienceUserOld> players = new HashSet<>();

	public void addPlayer(Player player) {
		AmbienceUserOld user = new AmbienceUserOld(player);

		// init events (setup cooldowns), so they don't all occur at once for the first time
		// TODO

		players.add(user);
	}

	public void removePlayer(Player player) {
		for (AmbienceUserOld user : new HashSet<>(players)) {
			if (user.getPlayer().getUniqueId().equals(player.getUniqueId()))
				user.getSoundPlayer().stopSounds();
			players.remove(user);
		}
	}

	@Override
	public void onStart() {

	}

	@Override
	public void onStop() {

	}
}
