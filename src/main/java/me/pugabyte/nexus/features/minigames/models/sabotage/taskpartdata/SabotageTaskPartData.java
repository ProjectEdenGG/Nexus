package me.pugabyte.nexus.features.minigames.models.sabotage.taskpartdata;

import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.sabotage.TaskPart;

public abstract class SabotageTaskPartData extends TaskPartData {
	public SabotageTaskPartData(TaskPart task) {
		super(task);
	}

	public abstract int getDuration();

	public abstract boolean hasTask();

	public void task(Match match) {}
}
