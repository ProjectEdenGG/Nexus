package gg.projecteden.nexus.features.minigames.models.scoreboards;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.parchment.sidebar.Sidebar;
import gg.projecteden.parchment.sidebar.SidebarLayout;
import gg.projecteden.parchment.sidebar.SidebarStage;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MinigamerSidebar implements MinigameScoreboard {
	private final Match match;
	private final Map<Minigamer, MinigamerSidebarLayout> scoreboards = new HashMap<>();

	public MinigamerSidebar(Match match) {
		this.match = match;
	}

	private MinigamerSidebarLayout createScoreboard(Minigamer minigamer) {
		return new MinigamerSidebarLayout(minigamer);
	}

	@Override
	public void update() {
		match.getMinigamers().forEach(minigamer ->
				scoreboards.computeIfAbsent(minigamer, $ ->
						createScoreboard(minigamer)));

		scoreboards.forEach(((minigamer, layout) -> layout.refresh()));
	}

	@Override
	public void handleJoin(Minigamer minigamer) {
		scoreboards.put(minigamer, createScoreboard(minigamer));
		Sidebar.get(minigamer.getPlayer()).applyLayout(scoreboards.get(minigamer));
		update();
	}

	@Override
	public void handleQuit(Minigamer minigamer) {
		Sidebar.get(minigamer.getPlayer()).applyLayout(null);
		scoreboards.remove(minigamer);
		update();
	}

	@Override
	public void handleEnd() {
		scoreboards.forEach((minigamer, scoreboard) -> Sidebar.get(minigamer.getPlayer()).applyLayout(null));
		scoreboards.clear();
	}

	@AllArgsConstructor
	public class MinigamerSidebarLayout extends SidebarLayout {

		Minigamer minigamer;

		@Override
		protected void setup(SidebarStage stage) {
			stage.setTitle(match.getMechanic().getScoreboardTitle(match));

			AtomicInteger lineNum = new AtomicInteger();
			match.getMechanic().getScoreboardLines(minigamer).forEach((line, score) -> {
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
