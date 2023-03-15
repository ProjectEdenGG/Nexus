package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.nexus.features.minigames.mechanics.common.DeathmatchMechanic;
import org.bukkit.Material;
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
				point.getRegion();
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
