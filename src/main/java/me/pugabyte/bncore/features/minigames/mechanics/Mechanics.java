package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.features.minigames.mechanics.capturetheflag.CaptureTheFlag;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;

public enum Mechanics {
	CAPTURE_THE_FLAG(new CaptureTheFlag()),
	ONE_IN_THE_QUIVER(new OneInTheQuiver()),
	PAINTBALL(new Paintball());

	private Mechanic mechanic;

	Mechanics(Mechanic mechanic) {
		this.mechanic = mechanic;
	}

	public Mechanic get() {
		return mechanic;
	}

}
