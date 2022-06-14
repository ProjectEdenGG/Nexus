package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.arenas.CheckpointArena;
import gg.projecteden.nexus.models.checkpoint.CheckpointService;
import gg.projecteden.nexus.models.checkpoint.RecordTotalTime;
import gg.projecteden.nexus.utils.JsonBuilder;
import kotlin.Pair;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
public class CheckpointData extends MatchData {
	private final CheckpointService service = new CheckpointService();
	private final Map<UUID, Map<Integer, Instant>> checkpointTimes = new HashMap<>();
	private final Map<UUID, Instant> startTimes = new HashMap<>();
	private final Map<UUID, RecordTotalTime> bestTotalTimesCache = new HashMap<>();
	private RecordTotalTime bestTotalTimeCache = null;
	private final Set<UUID> autoresetting = new HashSet<>();

	public CheckpointData(Match match) {
		super(match);
	}

	public static String formatChatTime(Duration duration) {
		StringBuilder sb = new StringBuilder();
		if (duration.toHoursPart() > 0)
			sb.append(duration.toHoursPart()).append("h ");
		if (duration.toMinutesPart() > 0)
			sb.append(duration.toMinutesPart()).append("m ");
		sb.append(String.format("%.2fs", duration.toSecondsPart() + (duration.toNanosPart() / 1000000000.0)));
		return sb.toString();
	}

	public static String formatLiveTime(Duration liveTime, @Nullable Duration best) {
		String output = "%d:%02d:%02d.%02d".formatted(liveTime.toHours(), liveTime.toMinutesPart(), liveTime.toSecondsPart(), liveTime.toMillisPart());
		if (best != null) {
			Duration delta = liveTime.minus(best);
			String formattedDelta = (delta.isNegative() ? "&a" : "&c") + "%+.0f".formatted(delta.toMillis() / 1000.0);
			output += " &7(" + formattedDelta + "&7)";
		}
		return output;
	}

	private void updateBestTimeCaches(Minigamer minigamer) {
		bestTotalTimesCache.put(minigamer.getUuid(), service.get(minigamer).getBestTotalTime(this));
		bestTotalTimeCache = CheckpointService.getBestTotalTime(this);
	}

	public void initialize(Minigamer minigamer) {
		checkpointTimes.put(minigamer.getUuid(), new HashMap<>());
		startTimes.put(minigamer.getUuid(), Instant.now());
		updateBestTimeCaches(minigamer);
	}

	public void onWin(Minigamer minigamer, Instant now) {
		// compute total time
		Instant startTime = startTimes.get(minigamer.getUuid());
		Duration time = Duration.between(startTime, now);
		// compute checkpoint times
		Map<Integer, Instant> checkpointInstants = checkpointTimes.get(minigamer.getUuid());
		Map<Integer, Duration> checkpoints = new HashMap<>();
		if (checkpointInstants != null)
			checkpointInstants.forEach((chkptId, chkptTime) -> checkpoints.put(chkptId, Duration.between(chkptTime, now)));
		// save data
		service.get(minigamer).recordTotalTime(this, time, checkpoints);
		updateBestTimeCaches(minigamer);
	}

	public Integer getCheckpointId(Minigamer minigamer) {
		Map<Integer, Instant> checkpoints = checkpointTimes.get(minigamer.getUuid());
		if (checkpoints == null || checkpoints.isEmpty()) return null;
		return checkpoints.keySet().stream().max(Integer::compareTo).orElse(null);
	}

	public void setCheckpoint(Minigamer minigamer, int id) {
		int currentId = Objects.requireNonNullElse(getCheckpointId(minigamer), 0);
		if (currentId < id) {
			minigamer.tell("Checkpoint saved (&e#" + id + "&3)");
			Instant now = Instant.now();
			checkpointTimes
				.computeIfAbsent(minigamer.getUuid(), k -> new HashMap<>())
				.put(id, now);
			service.get(minigamer).recordCheckpointTime(this, id, Duration.between(startTimes.get(minigamer.getUuid()), now));
		}
	}

	public void toCheckpoint(Minigamer minigamer) {
		CheckpointArena arena = minigamer.getMatch().getArena();

		if (autoresetting.contains(minigamer.getUuid()))
			reset(minigamer);
		else if (getCheckpointId(minigamer) != null)
			minigamer.teleportAsync(arena.getCheckpoint(getCheckpointId(minigamer)));
		else
			reset(minigamer);
	}

	private void reset(Minigamer minigamer) {
		clearCheckpoints(minigamer);
		minigamer.teleportAsync(minigamer.getTeam().getSpawnpoints().get(0));
		minigamer.setScore(0);
		startTimes.put(minigamer.getUuid(), Instant.now());
	}

	public void clearData(Minigamer minigamer) {
		clearCheckpoints(minigamer);
		clearAutoreset(minigamer);
		clearStartTime(minigamer);
	}

	private void clearCheckpoints(Minigamer minigamer) {
		checkpointTimes.remove(minigamer.getUuid());
	}

	private void clearAutoreset(Minigamer minigamer) {
		autoresetting.remove(minigamer.getUuid());
	}

	private void clearStartTime(Minigamer minigamer) {
		startTimes.remove(minigamer.getUuid());
	}

	public void autoreset(Minigamer minigamer, Boolean autoreset) {
		UUID uuid = minigamer.getUuid();

		if (autoreset == null)
			autoreset = !autoresetting.contains(uuid);

		if (autoreset)
			autoresetting.add(uuid);
		else
			autoresetting.remove(uuid);
	}

	public boolean isAutoresetting(Minigamer minigamer) {
		return autoresetting.contains(minigamer.getUuid());
	}

	public Duration calculateTotalTime(Minigamer minigamer, @Nullable Instant endTime) {
		if (endTime == null) endTime = Instant.now();
		Instant startTime = startTimes.get(minigamer.getUuid());
		if (startTime == null) return Duration.ZERO;
		return Duration.between(startTime, endTime);
	}

	public Duration calculateSplitTime(Minigamer minigamer, @Nullable Instant endTime) {
		if (endTime == null) endTime = Instant.now();
		Integer checkpointId = getCheckpointId(minigamer);
		Instant startTime = checkpointId == null ? startTimes.get(minigamer.getUuid()) : checkpointTimes.get(minigamer.getUuid()).get(checkpointId);
		if (startTime == null) return Duration.ZERO;
		return Duration.between(startTime, endTime);
	}

	public List<Pair<Integer, Duration>> calculateCheckpointTimes(Minigamer minigamer, @Nullable Instant endTime) {
		if (!checkpointTimes.containsKey(minigamer.getUuid()))
			return Collections.emptyList();

		if (endTime == null)
			endTime = Instant.now();

		List<Pair<Integer, Instant>> times = checkpointTimes.get(minigamer.getUuid()).entrySet()
			.stream()
			.map(entry -> new Pair<>(entry.getKey(), entry.getValue()))
			.sorted(Comparator.comparingInt(Pair<Integer, Instant>::getFirst))
			.collect(Collectors.toList());

		times.add(0, new Pair<>(0, startTimes.get(minigamer.getUuid())));
		times.add(new Pair<>(-1, endTime));

		List<Pair<Integer, Duration>> durations = new ArrayList<>(times.size());
		for (int i = 1; i < times.size(); i++) {
			Pair<Integer, Instant> from = times.get(i - 1);
			Pair<Integer, Instant> to = times.get(i);
			Duration duration = Duration.between(from.getSecond(), to.getSecond());
			durations.add(new Pair<>(to.getFirst(), duration));
		}
		return durations;
	}

	public String formatTotalChatTime(Minigamer minigamer, @Nullable Instant endTime) {
		return formatChatTime(calculateTotalTime(minigamer, endTime));
	}

	public String formatTotalLiveTime(Minigamer minigamer, @Nullable Instant endTime) {
		RecordTotalTime record = bestTotalTimesCache.get(minigamer.getUuid());
		return formatLiveTime(calculateTotalTime(minigamer, endTime), record == null ? null : record.getTime());
	}

	public String formatSplitTime(Minigamer minigamer, @Nullable Instant endTime) {
		return formatLiveTime(calculateSplitTime(minigamer, endTime), null); // TODO split comparison
	}

	public String formatTotalBestTime(Minigamer minigamer) {
		RecordTotalTime record = bestTotalTimesCache.get(minigamer.getUuid());
		return record == null ? "&7N/A" : "&e" + formatLiveTime(record.getTime(), null);
	}

	public @NotNull HoverEvent<Component> formatCheckpointTimesHoverText(Minigamer minigamer, @Nullable Instant endTime) {
		JsonBuilder builder = new JsonBuilder("Individual Checkpoint Times", NamedTextColor.DARK_AQUA);
		for (Pair<Integer, Duration> pair : calculateCheckpointTimes(minigamer, endTime)) {
			String checkpoint = pair.getFirst() == -1 ? "End" : "Checkpoint #" + pair.getFirst();
			builder
				.newline()
				.next(checkpoint + ": ", NamedTextColor.GOLD)
				.next(formatChatTime(pair.getSecond()), NamedTextColor.YELLOW);
		}
		return builder.build().asHoverEvent();
	}
}
