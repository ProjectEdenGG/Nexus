package me.pugabyte.bncore.features.minigames.models.scoreboards;

import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.utils.BNScoreboard;

import java.util.HashMap;
import java.util.Map;

public class MinigamerScoreboard implements MinigameScoreboard {
	private Match match;
	private Map<Minigamer, BNScoreboard> scoreboards = new HashMap<>();

	public MinigamerScoreboard(Match match) {
		this.match = match;
	}

	public BNScoreboard createScoreboard(Minigamer minigamer) {
		return new BNScoreboard(match.getArena().getMechanic().getScoreboardTitle(match), minigamer.getPlayer());
	}

	@Override
	public void update() {
		scoreboards.forEach((minigamer, scoreboard) ->
				scoreboard.setLines(match.getArena().getMechanic().getScoreboardLines(minigamer)));

		match.getMinigamers().forEach(minigamer ->
				scoreboards.computeIfAbsent(minigamer, $ ->
						createScoreboard(minigamer)));
	}

	@Override
	public void handleJoin(Minigamer minigamer) {
		createScoreboard(minigamer);
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
