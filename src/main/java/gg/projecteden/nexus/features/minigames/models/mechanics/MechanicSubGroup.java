package gg.projecteden.nexus.features.minigames.models.mechanics;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public enum MechanicSubGroup {
	PARKOUR(MechanicType.PARKOUR, MechanicType.XRUN),
	QUAKE(MechanicType.QUAKE, MechanicType.DOGFIGHTING),
	SPLEEF(MechanicType.SPLEEF, MechanicType.SPLEGG),
	MASTERMIND(MechanicType.MASTERMIND), // , MechanicType.MEGAMIND, MechanicType.MULTIMIND
	CAPTURE_THE_FLAG(MechanicType.CAPTURE_THE_FLAG, MechanicType.FLAG_RUSH, MechanicType.SIEGE),
	TEAM_DEATHMATCH(MechanicType.TEAM_DEATHMATCH, MechanicType.FOUR_TEAM_DEATHMATCH),
	;

	@Getter
	private final List<MechanicType> mechanics;

	MechanicSubGroup(MechanicType... mechanics) {
		this.mechanics = Arrays.asList(mechanics);
	}
}
