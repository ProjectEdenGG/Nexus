package gg.projecteden.nexus.features.minigolf.models.events;

import gg.projecteden.nexus.features.minigolf.models.blocks.ModifierBlockType;
import gg.projecteden.nexus.models.minigolf.GolfBall;
import lombok.Getter;

public class MiniGolfBallModifierBlockEvent extends MiniGolfBallMoveEvent {
	@Getter
	private final ModifierBlockType modifierBlockType;

	public MiniGolfBallModifierBlockEvent(GolfBall golfBall, ModifierBlockType modifierBlockType) {
		super(golfBall);
		this.modifierBlockType = modifierBlockType;
	}
}
