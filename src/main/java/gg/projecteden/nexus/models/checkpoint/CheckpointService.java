package gg.projecteden.nexus.models.checkpoint;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;
import gg.projecteden.nexus.utils.Tasks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(CheckpointUser.class)
public class CheckpointService extends MongoPlayerService<CheckpointUser> {
	private final static Map<UUID, CheckpointUser> cache = new ConcurrentHashMap<>();
	private final static Map<String, RecordTotalTime> globalBestTimes = new ConcurrentHashMap<>();
	private final static Map<String, Map<Integer, CheckpointValue>> globalBestCheckpointTimes = new ConcurrentHashMap<>();
	private static boolean globalBestTimesInitialized = false;

	public CheckpointService() {
		initCache();
	}

	public Map<UUID, CheckpointUser> getCache() {
		return cache;
	}

	@Override
	public void clearCache() {
		super.clearCache();
		globalBestTimes.clear();
	}

	private void initCache() {
		if (!globalBestTimesInitialized) {
			globalBestTimesInitialized = true;
			Tasks.async(() -> {
				for (CheckpointUser user : getAll()) {
					user.getBestTotalTimes().forEach(CheckpointService::recordBestTotalTime);
					user.getBestCheckpointTimes().forEach((arena, map) ->
						map.forEach((checkpoint, value) -> recordBestCheckpointTime(arena, checkpoint, value)));
				}
			});
		}
	}

	public static void removeBestTime(String arena, CheckpointUser user) {
		RecordTotalTime recordTotalTime = globalBestTimes.get(arena);
		// TODO GRIFFIN
	}

	public static void recordBestTotalTime(String arena, RecordTotalTime time) {
		if (!globalBestTimes.containsKey(arena) || globalBestTimes.get(arena).compareTo(time) > 0)
			globalBestTimes.put(arena, time);
	}

	public static void recordBestCheckpointTime(String arena, Integer checkpoint, CheckpointValue value) {
		final var times = CheckpointService.globalBestCheckpointTimes.computeIfAbsent(arena, $ -> new ConcurrentHashMap<>());
		if (!times.containsKey(checkpoint) || times.get(checkpoint).compareTo(value) > 0)
			times.put(checkpoint, value);
	}

	public static @Nullable RecordTotalTime getBestTotalTime(String arena) {
		return globalBestTimes.get(arena);
	}

	public List<RecordTotalTime> getBestTotalTimes(String arena) {
		return getAll().stream()
			.map(user -> user.getBestTotalTime(arena))
			.filter(Objects::nonNull)
			.sorted()
			.toList();
	}

	public static Map<Integer, CheckpointValue> getBestCheckpointTimes(@NotNull String arena) {
		return globalBestCheckpointTimes.get(arena);
	}

	// boilerplate

	public static @Nullable RecordTotalTime getBestTotalTime(Arena arena) {
		return getBestTotalTime(arena.getName());
	}

	public List<RecordTotalTime> getBestTotalTimes(Arena arena) {
		return getBestTotalTimes(arena.getName());
	}

	public static Map<Integer, CheckpointValue> getBestCheckpointTimes(@NotNull Arena arena) {
		return getBestCheckpointTimes(arena.getName());
	}

	public static @Nullable RecordTotalTime getBestTotalTime(Match match) {
		return getBestTotalTime(match.getArena());
	}

	public List<RecordTotalTime> getBestTotalTimes(Match match) {
		return getBestTotalTimes(match.getArena());
	}

	public static Map<Integer, CheckpointValue> getBestCheckpointTimes(@NotNull Match match) {
		return getBestCheckpointTimes(match.getArena());
	}

	public static @Nullable RecordTotalTime getBestTotalTime(MatchData matchData) {
		return getBestTotalTime(matchData.getMatch());
	}

	public List<RecordTotalTime> getBestTotalTimes(MatchData matchData) {
		return getBestTotalTimes(matchData.getMatch());
	}

	public static Map<Integer, CheckpointValue> getBestCheckpointTimes(@NotNull MatchData matchData) {
		return getBestCheckpointTimes(matchData.getMatch());
	}
}
