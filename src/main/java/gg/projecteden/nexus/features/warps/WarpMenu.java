package gg.projecteden.nexus.features.warps;

import gg.projecteden.nexus.models.buildcontest.BuildContest;
import gg.projecteden.nexus.models.buildcontest.BuildContestService;

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

	public int getSize() {
		if (this == MAIN) {
			BuildContest buildContest = new BuildContestService().get0();
			if (buildContest.isActive())
				return size + 2;
		}
		return size;
	}

}
