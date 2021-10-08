package gg.projecteden.nexus.features.minigames.models.scoreboards;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.utils.EdenScoreboard;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import org.bukkit.entity.Player;

public class MatchSidebar implements MinigameScoreboard {
	private final Match match;
	private final EdenScoreboard scoreboard;

	public MatchSidebar(Match match) {
		this.match = match;
		this.scoreboard = new EdenScoreboard(match.getMechanic().getScoreboardTitle(match));;
	}

	@Override
	public void update() {
		scoreboard.setTitle(match.getMechanic().getScoreboardTitle(match));
		scoreboard.setLines(match.getMechanic().getScoreboardLines(match));

		for (Player player : OnlinePlayers.getAll())
			if (!match.getPlayers().contains(player))
				scoreboard.unsubscribe(player);

		scoreboard.subscribe(match.getPlayers());
	}

	@Override
	public void handleJoin(Minigamer minigamer) {
		scoreboard.subscribe(minigamer.getPlayer());
		update();
	}

	@Override
	public void handleQuit(Minigamer minigamer) {
		scoreboard.unsubscribe(minigamer.getPlayer());
		update();
	}

	@Override
	public void handleEnd() {
		scoreboard.delete();
	}

}
