package me.pugabyte.bncore.features.warps;

import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;

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
		if (this == MAIN) {
			SettingService service = new SettingService();
			Setting bc = service.get("buildcontest", "info");
			if (bc != null && (Boolean) bc.getJson().get("active"))
				return size + 2;
		}
		return size;
	}


}
