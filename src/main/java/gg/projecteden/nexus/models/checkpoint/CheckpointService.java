package gg.projecteden.nexus.models.checkpoint;

import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;
import gg.projecteden.nexus.utils.Tasks;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(CheckpointUser.class)
public class CheckpointService extends MongoPlayerService<CheckpointUser> {
	private final static Map<UUID, CheckpointUser> cache = new ConcurrentHashMap<>();
	private final static Map<String, RecordTotalTime> globalBestTimes = new ConcurrentHashMap<>();
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
					for (Map.Entry<String, Duration> entry : user.getBestTotalTimes().entrySet()) {
						recordBestTotalTime(entry.getKey(), new RecordTotalTime(user.getUuid(), entry.getValue()));
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

	public static @Nullable RecordTotalTime getBestTotalTime(String arena) {
		return globalBestTimes.get(arena);
	}

	public CompletableFuture<List<RecordTotalTime>> getBestTotalTimes(String arena) {
		return CompletableFuture.supplyAsync(
			() -> getAll().stream()
				.filter(user -> user.getBestTotalTime(arena) != null)
				.map(user -> new RecordTotalTime(user.getUuid(), user.getBestTotalTime(arena)))
				.sorted()
				.toList(),
			Tasks::async
		);
	}

	// boilerplate

	public static @Nullable RecordTotalTime getBestTotalTime(Arena arena) {
		return getBestTotalTime(arena.getName());
	}

	public CompletableFuture<List<RecordTotalTime>> getBestTotalTimes(Arena arena) {
		return getBestTotalTimes(arena.getName());
	}

	public static @Nullable RecordTotalTime getBestTotalTime(Match match) {
		return getBestTotalTime(match.getArena());
	}

	public CompletableFuture<List<RecordTotalTime>> getBestTotalTimes(Match match) {
		return getBestTotalTimes(match.getArena());
	}

	public static @Nullable RecordTotalTime getBestTotalTime(MatchData matchData) {
		return getBestTotalTime(matchData.getMatch());
	}

	public CompletableFuture<List<RecordTotalTime>> getBestTotalTimes(MatchData matchData) {
		return getBestTotalTimes(matchData.getMatch());
	}
}
