package gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.taskpartdata;

import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.TaskPart;

public abstract class SabotageTaskPartData extends TaskPartData {
	public SabotageTaskPartData(TaskPart task) {
		super(task);
	}

	public abstract int getDuration();
}
