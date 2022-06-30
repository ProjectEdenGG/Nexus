package gg.projecteden.nexus.features.survival;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.SoundUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AmbientSounds {
	private final static long startDelay = TickTime.SECOND.x(5);
	//
	private final static Map<Player, Integer> undergroundTaskMap = new HashMap<>();
	private final static Sound undergroundLoop = Sound.AMBIENT_CRIMSON_FOREST_LOOP;
	private final static List<Sound> undergroundSounds = Arrays.asList(Sound.AMBIENT_CAVE,
		Sound.AMBIENT_CRIMSON_FOREST_ADDITIONS, Sound.AMBIENT_CRIMSON_FOREST_MOOD);

	public void onStop() {
		undergroundTaskMap.forEach((player, integer) -> SoundUtils.stopSound(player, undergroundLoop));
	}

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
			for (Player player : OnlinePlayers.where().world(SurvivalEffects.worldguard.getWorld()).region(SurvivalEffects.baseRegion).get()) {

				boolean inArea;
				boolean onList;

				// Underground
				inArea = isApplicable(player, AmbientSoundType.UNDERGROUND);
				onList = undergroundTaskMap.containsKey(player);

				if (inArea && !onList) {
					startLoop(player, AmbientSoundType.UNDERGROUND);
				} else if (!inArea && onList) {
					stopLoop(player, AmbientSoundType.UNDERGROUND);
				}
			}
		});
	}

	private void startLoop(Player player, AmbientSoundType type) {
		if (type == AmbientSoundType.UNDERGROUND) {
			int taskId = Tasks.repeat(0, TickTime.SECOND.x(37), () ->
				new SoundBuilder(undergroundLoop).receiver(player).category(SoundCategory.AMBIENT).volume(5).play());

			undergroundTaskMap.put(player, taskId);
		}
	}

	private void stopLoop(Player player, AmbientSoundType type) {
		Integer taskId = null;
		Sound sound = null;
		if (AmbientSoundType.UNDERGROUND == type) {
			taskId = undergroundTaskMap.get(player);
			undergroundTaskMap.remove(player);

			sound = undergroundLoop;
		}

		if (taskId != null)
			Tasks.cancel(taskId);

		SoundUtils.stopSound(player, sound, SoundCategory.AMBIENT);
	}

	private boolean isApplicable(Player player, AmbientSoundType type) {
		String regionRegex = SurvivalEffects.baseRegion;
		Location location = player.getLocation();
		switch (type) {
			case UNDERGROUND -> regionRegex += "_underground_";
			case DUNGEON -> regionRegex += "_dungeon_";
		}

		@NotNull Set<ProtectedRegion> regions = SurvivalEffects.worldguard.getRegionsLikeAt(regionRegex + ".*", location);
		if (regions.isEmpty())
			return false;

		for (ProtectedRegion region : regions) {
			if (region.getId().contains("_exclude_"))
				return false;
		}

		return true;
	}

	public enum AmbientSoundType {
		UNDERGROUND,
		DUNGEON
	}
}
