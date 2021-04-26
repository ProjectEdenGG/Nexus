package me.pugabyte.nexus.features.minigames.models;

import eden.utils.TimeUtils.Time;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import me.pugabyte.nexus.features.minigames.models.Match.MatchTasks.MatchTaskType;
import me.pugabyte.nexus.features.minigames.models.events.matches.lobbies.LobbyTimerTickEvent;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@SerializableAs("Lobby")
public class Lobby implements ConfigurationSerializable {
	@NonNull
	private int waitTime = 30;
	private Location location;
	private boolean timerStarted;

	public Lobby() {
		this(new HashMap<>());
	}

	public Lobby(Map<String, Object> map) {
		this.waitTime = (Integer) map.getOrDefault("waitTime", waitTime);
		this.location = (Location) map.getOrDefault("location", location);
	}

	@Override
	public Map<String, Object> serialize() {
		return new LinkedHashMap<String, Object>() {{
			put("waitTime", getWaitTime());
			put("location", getLocation());
		}};
	}

	public void join(Minigamer minigamer) {
		minigamer.teleport(location);
		minigamer.clearState();
		if (!timerStarted)
			new Lobby.LobbyTimer(this, minigamer.getMatch(), waitTime);
	}

	private class LobbyTimer {
		private final Lobby lobby;
		private final Match match;
		private final Arena arena;
		private int time;
		private final List<Integer> broadcasts = Arrays.asList(60, 30, 15, 5, 4, 3, 2, 1);
		private int taskId;

		LobbyTimer(Lobby lobby, Match match, int time) {
			this.lobby = lobby;
			this.match = match;
			this.arena = match.getArena();
			this.time = time;
			start();
		}

		private void start() {
			taskId = match.getTasks().repeat(0, Time.SECOND, () -> {
				if (match.isStarted()) {
					stop();
					return;
				}

				int current = match.getMinigamers().size();
				int min = arena.getMinPlayers();
				int left = min - current;
				if (current < min) {
					match.broadcast("&7Waiting for &e" + left + " &7more players");
					stop();
					return;
				}

				if (!timerStarted)
					match.broadcast("&7Starting in &e" + time + " &7seconds");
				timerStarted = true;

				if (--time > 0) {
					LobbyTimerTickEvent event = new LobbyTimerTickEvent(match, lobby, time);
					event.callEvent();
					if (broadcasts.contains(time)) {
						match.broadcast("&e" + time + " &7seconds left...");
						match.getPlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, .75F, .6F));
					}
				} else if (time == 0) {
					stop();
					match.start();
				}
			});

			match.getTasks().register(MatchTaskType.LOBBY, taskId);
		}

		private void stop() {
			timerStarted = false;
			match.getTasks().cancel(taskId);
		}

	}

}
