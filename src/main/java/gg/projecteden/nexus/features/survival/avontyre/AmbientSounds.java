package gg.projecteden.nexus.features.survival.avontyre;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.survival.Survival;
import gg.projecteden.nexus.models.nerd.Nerd;
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
import java.util.UUID;

public class AmbientSounds {
	private final static long startDelay = TickTime.SECOND.x(5);
	//
	private final static Map<UUID, Integer> UNDERGROUND_TASK_MAP = new HashMap<>();
	private final static Sound UNDERGROUND_LOOP = Sound.AMBIENT_CRIMSON_FOREST_LOOP;
	private final static List<Sound> UNDERGROUND_SOUNDS = Arrays.asList(Sound.AMBIENT_CAVE,
		Sound.AMBIENT_CRIMSON_FOREST_ADDITIONS, Sound.AMBIENT_CRIMSON_FOREST_MOOD);

	public void onStop() {
		UNDERGROUND_TASK_MAP.forEach((uuid, integer) -> {
			var nerd = Nerd.of(uuid);
			if (nerd.isOnline())
				SoundUtils.stopSound(nerd.getOnlinePlayer(), UNDERGROUND_LOOP);
		});
	}

	public void onStart() {
		// Random sounds
		Tasks.repeat(startDelay, TickTime.SECOND.x(15), () -> {
			if (RandomUtils.chanceOf(50)) {
				Sound sound = RandomUtils.randomElement(UNDERGROUND_SOUNDS);
				Map<UUID, Integer> tempMap = new HashMap<>(UNDERGROUND_TASK_MAP);


				tempMap.forEach((uuid, integer) -> {
					var nerd = Nerd.of(uuid);
					if (nerd.isOnline()) {
						Player player = nerd.getOnlinePlayer();
						if (!isApplicable(player, AmbientSoundType.UNDERGROUND)) {
							stopLoop(player, AmbientSoundType.UNDERGROUND);
							return;
						}

						new SoundBuilder(sound).receiver(player).category(SoundCategory.AMBIENT).volume(1).pitch(.1).play();
					}
				});
			}
		});

		// Looping Sound Management
		Tasks.repeat(startDelay, TickTime.SECOND.x(1), () -> {
			for (Player player : getPlayers()) {

				boolean inArea;
				boolean onList;

				// Underground
				inArea = isApplicable(player, AmbientSoundType.UNDERGROUND);
				onList = UNDERGROUND_TASK_MAP.containsKey(player.getUniqueId());

				if (inArea && !onList) {
					startLoop(player, AmbientSoundType.UNDERGROUND);
				} else if (!inArea && onList) {
					stopLoop(player, AmbientSoundType.UNDERGROUND);
				}
			}
		});
	}

	private List<Player> getPlayers() {
		return OnlinePlayers.where().world(Survival.getWorld()).region(Survival.getSpawnRegion()).get();
	}

	private void startLoop(Player player, AmbientSoundType type) {
		if (type == AmbientSoundType.UNDERGROUND) {
			int taskId = Tasks.repeat(0, TickTime.SECOND.x(37), () ->
				new SoundBuilder(UNDERGROUND_LOOP).receiver(player).category(SoundCategory.AMBIENT).volume(5).play());

			UNDERGROUND_TASK_MAP.put(player.getUniqueId(), taskId);
		}
	}

	private void stopLoop(Player player, AmbientSoundType type) {
		Integer taskId = null;
		Sound sound = null;
		if (AmbientSoundType.UNDERGROUND == type) {
			taskId = UNDERGROUND_TASK_MAP.get(player.getUniqueId());
			UNDERGROUND_TASK_MAP.remove(player.getUniqueId());

			sound = UNDERGROUND_LOOP;
		}

		if (taskId != null)
			Tasks.cancel(taskId);

		SoundUtils.stopSound(player, sound, SoundCategory.AMBIENT);
	}

	private boolean isApplicable(Player player, AmbientSoundType type) {
		if (!player.getWorld().equals(Survival.getWorld()))
			return false;

		String regionRegex = Survival.getSpawnRegion();
		Location location = player.getLocation();
		switch (type) {
			case UNDERGROUND -> regionRegex += "_underground_";
			case DUNGEON -> regionRegex += "_dungeon_";
		}

		@NotNull Set<ProtectedRegion> regions = Survival.worldguard().getRegionsLikeAt(regionRegex + ".*", location);
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
