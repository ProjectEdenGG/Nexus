package gg.projecteden.nexus.features.ambience;

import gg.projecteden.nexus.features.ambience.managers.common.AmbienceManagers;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.ambience.AmbienceUser;
import gg.projecteden.nexus.models.ambience.AmbienceUserService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.NoArgsConstructor;
import org.bukkit.event.Listener;

import java.util.List;

@NoArgsConstructor
public class Ambience extends Feature implements Listener {
	private static final AmbienceUserService userService = new AmbienceUserService();

	@Override
	public void onStart() {
		AmbienceManagers.start();
		ambienceTask();
	}

	@Override
	public void onStop() {
		AmbienceManagers.stop();
	}

	public static void sendDebug(String message) {
		for (AmbienceUser user : getUsers()) {
			user.debug(message);
		}
	}

	private void ambienceTask() {
		Tasks.repeat(0, TickTime.TICK.x(2), () -> {
			for (AmbienceUser user : getUsers()) {
				user.getVariables().update();
				user.getSoundPlayer().update();
			}
			AmbienceManagers.tick();
		});

		Tasks.repeat(0, TickTime.SECOND.x(1), () -> {
			for (AmbienceUser user : getUsers())
				AmbienceManagers.update(user);
		});
	}

	public static List<AmbienceUser> getUsers() {
		return OnlinePlayers.where().rank(Rank.ADMIN).get().stream().map(userService::get).toList();
	}

}
