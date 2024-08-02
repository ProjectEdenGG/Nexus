package gg.projecteden.nexus.features.warps;

import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.models.buildcontest.BuildContestService;
import org.bukkit.entity.Player;

public enum WarpMenu {
	MAIN(4),
	SURVIVAL(3),
	LEGACY(5),
	MINIGAMES(3),
	CREATIVE(),
	SKYBLOCK(),
	OTHER(4),
	BUILD_CONTESTS(3);

	private int size = 0;

	WarpMenu() {
	}

	WarpMenu(int size) {
		this.size = size;
	}

	public int getSize(Player player) {
		if (this == MAIN) {
			var event = EdenEvent.getActiveEvent(player);
			var buildContest = new BuildContestService().get0();
			if (event != null || buildContest.isActive())
				return size + 2;
		}
		return size;
	}

}
