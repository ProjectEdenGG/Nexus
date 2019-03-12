package me.pugabyte.bncore.features.minigames.models;

import lombok.Data;
import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.models.events.lobbies.LobbyTimerTickEvent;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.List;

@Data
public class Lobby {
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

	private class LobbyTimer {
		private Lobby lobby;
		private Match match;
		private Arena arena;
		private int time;
		private List<Integer> broadcasts = Arrays.asList(60, 30, 15, 5, 4, 3, 2, 1);
		private BNCore bnCore = BNCore.getInstance();
		private int taskId;

		LobbyTimer(Lobby lobby, Match match, int time) {
			this.lobby = lobby;
			this.match = match;
			this.arena = match.getArena();
			this.time = time;
			start();
		}

		private void start() {
			taskId = bnCore.getServer().getScheduler().scheduleSyncRepeatingTask(bnCore, () -> {
				int current = match.getMinigamers().size();
				int min = arena.getMinPlayers();
				int left = min - current;
				if (current < min) {
					match.broadcast("Waiting for " + left + " more players");
					stop();
					return;
				}

				if (!match.isStarted())
					timerStarted = true;

				if (--time > 0) {
					LobbyTimerTickEvent event = new LobbyTimerTickEvent(lobby, match, time);
					BNCore.callEvent(event);
					if (broadcasts.contains(time)) {
						match.broadcast(time + " seconds left...");
					}
				} else if (time == 0) {
					match.start();
					stop();
				}
			}, 1, 20);
		}

		private void stop() {
			timerStarted = false;
			bnCore.getServer().getScheduler().cancelTask(taskId);
		}
	}
}
