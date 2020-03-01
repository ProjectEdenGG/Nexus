package me.pugabyte.bncore.features.minigames.models.scoreboards;

import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.utils.BNScoreboard;

import java.util.HashMap;
import java.util.Map;

public class MinigamerSidebar implements MinigameScoreboard {
	private Match match;
	private Map<Minigamer, BNScoreboard> scoreboards = new HashMap<>();

	public MinigamerSidebar(Match match) {
		this.match = match;
	}

	private BNScoreboard createScoreboard(Minigamer minigamer) {
		return new BNScoreboard(
				minigamer.getName() + "-" + minigamer.getMatch().getArena().getName(),
				match.getArena().getMechanic().getScoreboardTitle(match),
				minigamer.getPlayer()
		);
	}

	@Override
	public void update() {
		match.getMinigamers().forEach(minigamer ->
				scoreboards.computeIfAbsent(minigamer, $ ->
						createScoreboard(minigamer)));

		scoreboards.forEach((minigamer, scoreboard) -> {
			scoreboard.setTitle(match.getArena().getMechanic().getScoreboardTitle(minigamer.getMatch()));
			scoreboard.setLines(match.getArena().getMechanic().getScoreboardLines(minigamer));
		});
	}

	@Override
	public void handleJoin(Minigamer minigamer) {
		scoreboards.put(minigamer, createScoreboard(minigamer));
	}

	@Override
	public void handleQuit(Minigamer minigamer) {
		scoreboards.computeIfPresent(minigamer, ($, scoreboard) -> {
			scoreboard.delete();
			return null;
		});
	}

	@Override
	public void handleEnd() {
		scoreboards.forEach((minigamer, scoreboard) -> scoreboard.delete());
		scoreboards.clear();
	}

}
