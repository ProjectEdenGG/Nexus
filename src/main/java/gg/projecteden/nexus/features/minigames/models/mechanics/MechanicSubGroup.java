package gg.projecteden.nexus.features.minigames.models.mechanics;

import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.nexus.features.minigames.menus.annotations.Scroll;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.customboundingbox.CustomBoundingBoxEntity;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum MechanicSubGroup {
	PARKOUR(MechanicType.PARKOUR, MechanicType.XRUN),
	QUAKE(MechanicType.QUAKE, MechanicType.DOGFIGHTING),
	SPLEEF(MechanicType.SPLEEF, MechanicType.SPLEGG),
//	MASTERMIND(MechanicType.MASTERMIND, MechanicType.MEGAMIND, MechanicType.MULTIMIND),
	CAPTURE_THE_FLAG(MechanicType.CAPTURE_THE_FLAG, MechanicType.FLAG_RUSH, MechanicType.SIEGE),
	TEAM_DEATHMATCH(MechanicType.TEAM_DEATHMATCH, MechanicType.FOUR_TEAM_DEATHMATCH),
	@Scroll BOARD_GAMES(MechanicType.BATTLESHIP, MechanicType.CONNECT4, MechanicType.TICTACTOE/*, MechanicType.CHECKERS*/),
	;

	private final List<MechanicType> mechanics;

	MechanicSubGroup(MechanicType... mechanics) {
		this.mechanics = Arrays.asList(mechanics);
	}

	public static boolean isParent(MechanicType mechanic) {
		try {
			valueOf(mechanic.name());
			return true;
		} catch (IllegalArgumentException ignore) {
			return false;
		}
	}

	public static MechanicSubGroup from(MechanicType mechanic) {
		for (MechanicSubGroup subGroup : values())
			if (subGroup.getMechanics().contains(mechanic))
				return subGroup;

		return null;
	}

	public static final String BOUNDING_BOX_ID_PREFIX = "minigames_lobby_mechanicsubgroup_";

	public static MechanicSubGroup from(CustomBoundingBoxEntity entity) {
		String id = entity.getId();
		if (Nullables.isNullOrEmpty(id))
			return null;

		if (!id.startsWith(BOUNDING_BOX_ID_PREFIX))
			throw new InvalidInputException("Mechanic ImageStand does not have expected prefix (found " + id + ", expected " + BOUNDING_BOX_ID_PREFIX + ")");

		final String mechanicName = id.replace(BOUNDING_BOX_ID_PREFIX, "");

		try {
			return MechanicSubGroup.valueOf(mechanicName.toUpperCase());
		} catch (IllegalArgumentException ex) {
			throw new InvalidInputException("&cMechanic &e" + mechanicName + " &cnot found");
		}
	}

}
