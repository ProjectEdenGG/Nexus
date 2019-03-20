package me.pugabyte.bncore.features.minigames.models.mechanics;

import me.pugabyte.bncore.features.minigames.mechanics.CaptureTheFlag;
import me.pugabyte.bncore.features.minigames.mechanics.FourTeamDeathmatch;
import me.pugabyte.bncore.features.minigames.mechanics.FreeForAll;
import me.pugabyte.bncore.features.minigames.mechanics.OneFlagCaptureTheFlag;
import me.pugabyte.bncore.features.minigames.mechanics.OneInTheQuiver;
import me.pugabyte.bncore.features.minigames.mechanics.Paintball;
import me.pugabyte.bncore.features.minigames.mechanics.TeamDeathmatch;

public enum MechanicType {
	CAPTURE_THE_FLAG(new CaptureTheFlag()),
	FOUR_TEAM_DEATHMATCH(new FourTeamDeathmatch()),
	FREE_FOR_ALL(new FreeForAll()),
	ONE_FLAG_CAPTURE_THE_FLAG(new OneFlagCaptureTheFlag()),
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
