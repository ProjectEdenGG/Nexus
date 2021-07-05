package me.pugabyte.nexus.features.minigolf.models.events;

import lombok.Getter;
import me.pugabyte.nexus.features.minigolf.models.GolfBall;
import me.pugabyte.nexus.features.minigolf.models.blocks.ModifierBlockType;

public class MiniGolfBallModifierBlockEvent extends MiniGolfBallMoveEvent {
	@Getter
	ModifierBlockType modifierBlockType;

	public MiniGolfBallModifierBlockEvent(GolfBall golfBall, ModifierBlockType modifierBlockType) {
		super(golfBall);
		this.modifierBlockType = modifierBlockType;
	}
}
