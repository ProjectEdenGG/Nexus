package me.pugabyte.nexus.features.ambience;

import eden.annotations.Environments;
import eden.utils.Env;
import eden.utils.TimeUtils.Time;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.ambience.managers.common.AmbienceManagers;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.models.ambience.AmbienceUser;
import me.pugabyte.nexus.models.ambience.AmbienceUserService;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.event.Listener;

import java.util.List;

@NoArgsConstructor
@Environments(Env.TEST)
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

	private void ambienceTask() {
		Tasks.repeat(0, Time.TICK.x(2), () -> {
			for (AmbienceUser user : getUsers())
				user.getVariables().update();
			AmbienceManagers.tick();
		});

		Tasks.repeat(0, Time.SECOND.x(1), () -> {
			for (AmbienceUser user : getUsers())
				AmbienceManagers.update(user);
		});
	}

	public List<AmbienceUser> getUsers() {
		return PlayerUtils.getOnlinePlayers().stream().map(userService::get).toList();
	}

}
