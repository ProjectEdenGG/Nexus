package me.pugabyte.bncore.features.minigames.models.scoreboards;

import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.utils.BNScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MatchScoreboard implements MinigameScoreboard {
	private Match match;
	private BNScoreboard scoreboard;

	public MatchScoreboard(Match match) {
		this.match = match;
		this.scoreboard = new BNScoreboard(match.getArena().getMechanic().getScoreboardTitle(match));;
	}

	@Override
	public void update() {
		scoreboard.setLines(match.getArena().getMechanic().getScoreboardLines(match));

		for (Player player : Bukkit.getOnlinePlayers())
			if (!match.getPlayers().contains(player))
				scoreboard.removePlayer(player);

		scoreboard.addPlayers(match.getPlayers());
	}

	@Override
	public void handleJoin(Minigamer minigamer) {
		scoreboard.addPlayer(minigamer.getPlayer());
		update();
	}

	@Override
	public void handleQuit(Minigamer minigamer) {
		scoreboard.removePlayer(minigamer.getPlayer());
		update();
	}

	@Override
	public void handleEnd() {
		scoreboard.delete();
		scoreboard = null;
	}

}
