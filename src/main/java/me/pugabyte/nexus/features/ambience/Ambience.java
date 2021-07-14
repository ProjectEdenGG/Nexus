package me.pugabyte.nexus.features.ambience;

import eden.utils.TimeUtils.Time;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.models.ambience.AmbienceUser;
import me.pugabyte.nexus.models.ambience.AmbienceUserService;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.event.Listener;

import java.util.List;

@NoArgsConstructor
public class Ambience extends Feature implements Listener {
	private static final AmbienceUserService userService = new AmbienceUserService();
	// TODO: make this dynamic
	@Getter
	private static final boolean windBlowing = true;
	@Getter
	public static final double windDir = Math.random() * 2 * Math.PI;
	@Getter
	public static double WIND_X = Math.sin(windDir);
	@Getter
	public static double WIND_Z = Math.cos(windDir);

	@Override
	public void onStart() {
		ParticleEffects.loadEffects();
		ambienceTask();
	}

	@Override
	public void onStop() {
		ParticleEffects.getActiveEffects().clear();
		// TODO: stop particles & sounds
	}


	private void ambienceTask() {
		Tasks.repeat(0, Time.TICK.x(2), () -> {
			for (AmbienceUser user : getUsers()) {
				user.getVariables().update();
			}

			ParticleEffects.tick();
		});

		Tasks.repeat(0, Time.SECOND.x(1), () -> {
			List<AmbienceUser> particleUsers = getUsers().stream().filter(AmbienceUser::isParticles).toList();
			for (AmbienceUser user : particleUsers) {
				ParticleEffects.update(user);
			}
		});
	}

	public List<AmbienceUser> getUsers() {
		return PlayerUtils.getOnlinePlayers().stream().map(userService::get).toList();
	}
}
