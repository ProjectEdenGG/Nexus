package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.nexus.features.minigames.models.events.matches.MatchBeginEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchTimerTickEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.DominationMatchData;
import gg.projecteden.nexus.features.minigames.models.matchdata.DominationMatchData.Point;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.Utils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class Domination extends TeamMechanic {

	@Override
	public @NotNull String getName() {
		return "Domination";
	}

	@Override
	public @NotNull String getDescription() {
		return "Control the points";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.BLUE_BANNER);
	}

	@Override
	public void onBegin(@NotNull MatchBeginEvent event) {
		super.onBegin(event);

		DominationMatchData matchData = event.getMatch().getMatchData();

		for (String letter : Utils.ALPHANUMERICS.split("")) {
			try {
				Point point = matchData.new Point(letter);
				matchData.getPoints().add(point);
			} catch (InvalidInputException ignore) {
				break;
			}
		}
	}

	@EventHandler
	public void on(MatchTimerTickEvent event) {
		DominationMatchData matchData = event.getMatch().getMatchData();
		matchData.getPoints().forEach(Point::tick);
	}

}
