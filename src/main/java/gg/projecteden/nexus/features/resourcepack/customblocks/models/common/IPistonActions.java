package gg.projecteden.nexus.features.resourcepack.customblocks.models.common;

public interface IPistonActions {

	PistonAction getPistonPushAction();

	PistonAction getPistonPullAction();

	enum PistonAction {
		MOVE,
		PREVENT,
		BREAK;
	}
}
