package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchBeginEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchTimerTickEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDisplayTimerEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.KingOfTheHillMatchData;
import gg.projecteden.nexus.features.minigames.models.matchdata.KingOfTheHillMatchData.Point;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Utils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class KingOfTheHill extends TeamMechanic {

	@Override
	public @NotNull String getName() {
		return "King of the Hill";
	}

	@Override
	public @NotNull String getDescription() {
		return "Control the point";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.BLUE_BANNER);
	}

	@Override
	public boolean isTestMode() {
		return true;
	}

	@Override
	public void onBegin(@NotNull MatchBeginEvent event) {
		super.onBegin(event);

		final Match match = event.getMatch();
		KingOfTheHillMatchData matchData = match.getMatchData();

		for (String letter : Utils.ALPHANUMERICS.split("")) {
			try {
				matchData.getPoints().add(matchData.new Point(letter));
			} catch (InvalidInputException ignore) {}
		}

		if (matchData.getPoints().size() < 1) {
			match.end();
			Nexus.severe("Could not find any points, ending");
		}

		matchData.shufflePoints();
		matchData.movePoint(false);
	}

	@Override
	public void onDisplayTimer(MinigamerDisplayTimerEvent event) {
		final KingOfTheHillMatchData matchData = event.getMatch().getMatchData();
		if (matchData.getPoints().size() == 1)
			return;

		if (event.getSeconds() > 60)
			event.setContents(new JsonBuilder(event.getContents()).next(" | &3Point moves in &e" + event.getSeconds() % 60 + "s"));
	}

	@EventHandler
	public void on(MatchTimerTickEvent event) {
		if (!(event.getMatch().getMatchData() instanceof KingOfTheHillMatchData matchData))
			return;

		if (matchData.getPoints().size() > 1)
			if (event.getTime() % 60 == 0)
				matchData.movePoint(true);

		matchData.getPoints().forEach(Point::tick);
	}

}
