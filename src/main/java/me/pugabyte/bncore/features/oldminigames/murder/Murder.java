package me.pugabyte.bncore.features.oldminigames.murder;

import me.pugabyte.bncore.BNCore;

public class Murder {
	public Murder() {
		BNCore.registerCommand("murder", new MurderCommand());
		BNCore.registerListener(new MurderListener());
	}
}
