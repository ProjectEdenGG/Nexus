package gg.projecteden.nexus.features.ambience.old;

import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.SoundUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gg.projecteden.nexus.features.ambience.old.AmbientSounds.AmbientSoundType.UNDERGROUND;

public class AmbientSounds extends Feature {
	private final static long startDelay = TickTime.SECOND.x(5);
	//
	private final static Map<Player, Integer> undergroundTaskMap = new HashMap<>();
	private final static Sound undergroundLoop = Sound.AMBIENT_CRIMSON_FOREST_LOOP;
	private final static List<Sound> undergroundSounds = Arrays.asList(Sound.AMBIENT_CAVE,
			Sound.AMBIENT_CRIMSON_FOREST_ADDITIONS, Sound.AMBIENT_CRIMSON_FOREST_MOOD);

	@Override
	public void onStop() {
		undergroundTaskMap.forEach((player, integer) -> SoundUtils.stopSound(player, undergroundLoop));
	}

	@Override
	public void onStart() {
		// Random sounds
		Tasks.repeat(startDelay, TickTime.SECOND.x(15), () -> {
			if (RandomUtils.chanceOf(50)) {
				Sound sound = RandomUtils.randomElement(undergroundSounds);
				Map<Player, Integer> tempMap = new HashMap<>(undergroundTaskMap);

				tempMap.forEach((player, integer) -> new SoundBuilder(sound).receiver(player).category(SoundCategory.AMBIENT).volume(1).pitch(.1).play());
			}
		});

		// Looping Sound Management
		Tasks.repeat(startDelay, TickTime.SECOND.x(1), () -> {
			for (Player player : OnlinePlayers.getAll()) {

				boolean inArea;
				boolean onList;

				// Underground
				inArea = isApplicable(player, UNDERGROUND);
				onList = undergroundTaskMap.containsKey(player);

				if (inArea && !onList) {
					startLoop(player, UNDERGROUND);
				} else if (!inArea && onList) {
					stopLoop(player, UNDERGROUND);
				}
			}
		});
	}

	private void startLoop(Player player, AmbientSoundType type) {
		if (type.equals(UNDERGROUND)) {
			int taskId = Tasks.repeat(0, TickTime.SECOND.x(37), () -> new SoundBuilder(undergroundLoop).receiver(player).category(SoundCategory.AMBIENT).volume(5).play());

			undergroundTaskMap.put(player, taskId);
		}
	}

	private void stopLoop(Player player, AmbientSoundType type) {
		Integer taskId = null;
		Sound sound = null;
		if (type.equals(UNDERGROUND)) {
			taskId = undergroundTaskMap.get(player);
			undergroundTaskMap.remove(player);

			sound = undergroundLoop;
		}

		if (taskId != null)
			Tasks.cancel(taskId);

		SoundUtils.stopSound(player, sound, SoundCategory.AMBIENT);
	}

	private boolean isApplicable(Player player, AmbientSoundType type) {
		WorldGuardUtils worldguard = new WorldGuardUtils(player);
		if (type.equals(UNDERGROUND)) {
			return !worldguard.getRegionsLikeAt(".*_underground", player.getLocation()).isEmpty();
		}

		return false;
	}

	public enum AmbientSoundType {
		UNDERGROUND,
		DUNGEON
	}


}
