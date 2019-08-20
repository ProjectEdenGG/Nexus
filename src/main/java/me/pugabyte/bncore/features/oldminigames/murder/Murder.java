package me.pugabyte.bncore.features.oldminigames.murder;

import me.pugabyte.bncore.BNCore;

public class Murder {
	public static final String PREFIX = BNCore.getPrefix("Murder");

	public Murder() {
		new MurderListener();
	}
}
