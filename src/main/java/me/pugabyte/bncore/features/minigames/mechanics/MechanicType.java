package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.features.minigames.mechanics.capturetheflag.CaptureTheFlag;
import me.pugabyte.bncore.features.minigames.mechanics.deathmatch.FourTeamDeathmatch;
import me.pugabyte.bncore.features.minigames.mechanics.deathmatch.TeamDeathmatch;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;

public enum MechanicType {
	CAPTURE_THE_FLAG(new CaptureTheFlag()),
	FOUR_TEAM_DEATHMATCH(new FourTeamDeathmatch()),
	ONE_IN_THE_QUIVER(new OneInTheQuiver()),
	PAINTBALL(new Paintball()),
	TEAM_DEATHMATCH(new TeamDeathmatch());

	private Mechanic mechanic;

	MechanicType(Mechanic mechanic) {
		this.mechanic = mechanic;
	}

	public Mechanic get() {
		return mechanic;
	}

}
