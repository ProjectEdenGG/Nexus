package me.pugabyte.nexus.features.minigames.models.mechanics.custom.sabotage;

import me.lucko.helper.scoreboard.ScoreboardTeam;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.Team;
import me.pugabyte.nexus.features.minigames.models.scoreboards.MinigameScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SabotageScoreboard implements MinigameScoreboard.ITeams {
	private final Match match;
	private final Map<SabotageTeam, ScoreboardTeam> scoreboards;

	public SabotageScoreboard(Match match) {
		this.match = match;
		Map<SabotageTeam, ScoreboardTeam> scoreboardMap = new HashMap<>();
		Arrays.stream(SabotageTeam.values()).forEach(sabotageTeam -> {
			ScoreboardTeam team = Minigames.getScoreboard().createTeam(match.getArena().getName() + "-" + sabotageTeam.name().toLowerCase(), false);
			team.setColor(ChatColor.RED);
			team.setCanSeeFriendlyInvisibles(false);
			team.setAllowFriendlyFire(false);
			scoreboardMap.put(sabotageTeam, team);
		});
		scoreboards = Map.copyOf(scoreboardMap);
	}

	public ScoreboardTeam getScoreboardTeam(Team team) {
		return scoreboards.get(SabotageTeam.of(team));
	}

	@Override
	public void update() {
		match.getMinigamers().forEach(minigamer -> {
			if (!minigamer.isAlive()) {
				scoreboards.values().forEach(scoreboardTeam -> scoreboardTeam.subscribe(minigamer.getPlayer()));
			} else if (minigamer.getTeam() != null) {
				ScoreboardTeam scoreboardTeam = getScoreboardTeam(minigamer.getTeam());
				scoreboardTeam.addPlayer(minigamer.getPlayer());
				scoreboardTeam.subscribe(minigamer.getPlayer());
			}
		});
	}

	@Override
	public void handleJoin(Minigamer minigamer) {
		// no late join in Sabotage
	}

	@Override
	public void handleQuit(Minigamer minigamer) {
		if (minigamer.getTeam() == null) return;
		ScoreboardTeam scoreboardTeam = getScoreboardTeam(minigamer.getTeam());
		scoreboardTeam.removePlayer(minigamer.getPlayer());
		scoreboardTeam.unsubscribe(minigamer.getPlayer());
	}

	@Override
	public void handleEnd() {
		scoreboards.forEach(($, scoreboardTeam) -> {
			scoreboardTeam.getPlayers().forEach(scoreboardTeam::removePlayer);
			Bukkit.getOnlinePlayers().forEach(scoreboardTeam::unsubscribe);
			Minigames.getScoreboard().removeTeam(scoreboardTeam.getId());
		});
	}
}
