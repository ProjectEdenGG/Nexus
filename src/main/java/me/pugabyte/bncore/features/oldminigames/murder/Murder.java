package me.pugabyte.bncore.features.oldminigames.murder;

import me.pugabyte.bncore.utils.Utils;

public class Murder {
	public static final String PREFIX = Utils.getPrefix("Murder");

	public Murder() {
		new MurderListener();
	}
}
