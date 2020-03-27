package me.pugabyte.bncore.features.warps.normal;

public enum WarpMenu {
	MAIN(4),
	SURVIVAL(5),
	MINIGAMES(5),
	CREATIVE(),
	SKYBLOCK(),
	OTHER(6),
	BUILD_CONTESTS(3);

	private int size = 0;

	WarpMenu() {
	}

	WarpMenu(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}
}
