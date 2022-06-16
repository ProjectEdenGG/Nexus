package gg.projecteden.nexus.models.checkpoint;

import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;
import gg.projecteden.nexus.utils.Tasks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ObjectClass(CheckpointUser.class)
public class CheckpointService extends MongoPlayerService<CheckpointUser> {
	private final static Map<UUID, CheckpointUser> cache = new ConcurrentHashMap<>();
	private final static Map<String, RecordTotalTime> globalBestTimes = new ConcurrentHashMap<>();
	private final static Map<CheckpointKey, CheckpointValue> globalBestCheckpointTimes = new ConcurrentHashMap<>();
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
					for (Map.Entry<String, RecordTotalTime> entry : user.getBestTotalTimes().entrySet()) {
						recordBestTotalTime(entry.getKey(), entry.getValue());
					}
					for (Map.Entry<CheckpointKey, CheckpointValue> entry : user.getBestCheckpointTimes().entrySet()) {
						recordBestCheckpointTime(entry.getKey(), entry.getValue());
					}
				}
			});
		}
	}

	public static void recordBestTotalTime(String arena, RecordTotalTime time) {
		if (!globalBestTimes.containsKey(arena) || globalBestTimes.get(arena).compareTo(time) > 0) {
			globalBestTimes.put(arena, time);
		}
	}

	public static void recordBestCheckpointTime(CheckpointKey key, CheckpointValue value) {
		if (!globalBestCheckpointTimes.containsKey(key) || globalBestCheckpointTimes.get(key).compareTo(value) > 0) {
			globalBestCheckpointTimes.put(key, value);
		}
	}

	public static @Nullable RecordTotalTime getBestTotalTime(String arena) {
		return globalBestTimes.get(arena);
	}

	public CompletableFuture<List<RecordTotalTime>> getBestTotalTimes(String arena) {
		return CompletableFuture.supplyAsync(
			() -> getAll().stream()
				.map(user -> user.getBestTotalTime(arena))
				.filter(Objects::nonNull)
				.sorted()
				.toList(),
			Tasks::async
		);
	}

	public static Map<Integer, CheckpointValue> getBestCheckpointTimes(@NotNull String arena) {
		return globalBestCheckpointTimes.entrySet().stream()
			.filter(entry -> entry.getKey().getArenaName().equals(arena))
			.collect(Collectors.toMap(entry -> entry.getKey().getCheckpoint(), Entry::getValue));
	}

	// boilerplate

	public static @Nullable RecordTotalTime getBestTotalTime(Arena arena) {
		return getBestTotalTime(arena.getName());
	}

	public CompletableFuture<List<RecordTotalTime>> getBestTotalTimes(Arena arena) {
		return getBestTotalTimes(arena.getName());
	}

	public static Map<Integer, CheckpointValue> getBestCheckpointTimes(@NotNull Arena arena) {
		return getBestCheckpointTimes(arena.getName());
	}

	public static @Nullable RecordTotalTime getBestTotalTime(Match match) {
		return getBestTotalTime(match.getArena());
	}

	public CompletableFuture<List<RecordTotalTime>> getBestTotalTimes(Match match) {
		return getBestTotalTimes(match.getArena());
	}

	public static Map<Integer, CheckpointValue> getBestCheckpointTimes(@NotNull Match match) {
		return getBestCheckpointTimes(match.getArena());
	}

	public static @Nullable RecordTotalTime getBestTotalTime(MatchData matchData) {
		return getBestTotalTime(matchData.getMatch());
	}

	public CompletableFuture<List<RecordTotalTime>> getBestTotalTimes(MatchData matchData) {
		return getBestTotalTimes(matchData.getMatch());
	}

	public static Map<Integer, CheckpointValue> getBestCheckpointTimes(@NotNull MatchData matchData) {
		return getBestCheckpointTimes(matchData.getMatch());
	}
}
