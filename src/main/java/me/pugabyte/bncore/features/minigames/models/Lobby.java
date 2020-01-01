package me.pugabyte.bncore.features.minigames.models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import me.pugabyte.bncore.features.minigames.models.events.lobbies.LobbyTimerTickEvent;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@SerializableAs("Lobby")
public class Lobby implements ConfigurationSerializable {
	@NonNull
	private Location location;
	@NonNull
	private int waitTime;
	private boolean timerStarted;

	public void join(Minigamer minigamer) {
		minigamer.teleport(location);
		minigamer.clearState();
		if (!timerStarted)
			new Lobby.LobbyTimer(this, minigamer.getMatch(), waitTime);
	}

	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		map.put("location", getLocation());
		map.put("waitTime", getWaitTime());

		return map;
	}

	public static Lobby deserialize(Map<String, Object> map) {
		return Lobby.builder()
				.location((Location) map.get("location"))
				.waitTime((int) map.get("waitTime"))
				.build();
	}

	private class LobbyTimer {
		private Lobby lobby;
		private Match match;
		private Arena arena;
		private int time;
		private List<Integer> broadcasts = Arrays.asList(60, 30, 15, 5, 4, 3, 2, 1);
		private int taskId;

		LobbyTimer(Lobby lobby, Match match, int time) {
			this.lobby = lobby;
			this.match = match;
			this.arena = match.getArena();
			this.time = time;
			start();
		}

		private void start() {
			taskId = match.getTasks().repeat(1, 20, () -> {
				int current = match.getMinigamers().size();
				int min = arena.getMinPlayers();
				int left = min - current;
				if (current < min) {
					match.broadcast("&7Waiting for &e" + left + " &7more players");
					stop();
					return;
				}

				if (!match.isStarted()){
					if(!timerStarted)
						match.broadcast("&7Starting in &e" + time + " &7seconds");
					timerStarted = true;
				}

				if (--time > 0) {
					LobbyTimerTickEvent event = new LobbyTimerTickEvent(lobby, match, time);
					Utils.callEvent(event);
					if (broadcasts.contains(time))
						match.broadcast("&e" + time + " &7seconds left...");
				} else if (time == 0) {
					stop();
					match.start();
				}
			});
		}

		private void stop() {
			timerStarted = false;
			match.getTasks().cancel(taskId);
		}

	}

}
