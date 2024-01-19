package gg.projecteden.nexus.features.minigames.models.scoreboards;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.parchment.sidebar.Sidebar;
import gg.projecteden.parchment.sidebar.SidebarLayout;
import gg.projecteden.parchment.sidebar.SidebarStage;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class MatchSidebar implements MinigameScoreboard {
	private final Match match;
	private final SidebarLayout layout;

	public MatchSidebar(Match match) {
		this.match = match;
		this.layout = new MatchSidebarLayout();
	}

	@Override
	public void update() {
		this.layout.refresh();
		for (Player player : OnlinePlayers.getAll())
			if (!match.getOnlinePlayers().contains(player))
				Sidebar.get(player).applyLayout(null);

		match.getOnlinePlayers().forEach(player -> Sidebar.get(player).applyLayout(layout));
	}

	@Override
	public void handleJoin(Minigamer minigamer) {
		Sidebar.get(minigamer.getPlayer()).applyLayout(layout);
		update();
	}

	@Override
	public void handleQuit(Minigamer minigamer) {
		Sidebar.get(minigamer.getPlayer()).applyLayout(null);
		update();
	}

	@Override
	public void handleEnd() {
		match.getOnlinePlayers().forEach(player -> {
			Sidebar.get(player).applyLayout(null);
		});
	}

	public class MatchSidebarLayout extends SidebarLayout {

		@Override
		protected void setup(SidebarStage stage) {
			stage.setTitle(match.getMechanic().getScoreboardTitle(match));

			AtomicInteger lineNum = new AtomicInteger();
			match.getMechanic().getScoreboardLines(match).forEach((line, score) -> {
				if (lineNum.get() >= 15) return;
				stage.setLine(lineNum.getAndIncrement(), line, match.getMechanic().useScoreboardNumbers() && score != Integer.MIN_VALUE ? "&c" + score : null);
			});
		}

		@Override
		protected void update(SidebarStage stage) {
			setup(stage);
		}
	}

}
