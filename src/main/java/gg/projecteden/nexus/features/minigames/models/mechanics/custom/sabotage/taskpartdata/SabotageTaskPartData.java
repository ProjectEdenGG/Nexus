package gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.taskpartdata;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.TaskPart;
import org.jetbrains.annotations.NotNull;

public abstract class SabotageTaskPartData extends TaskPartData {
	public SabotageTaskPartData(TaskPart task) {
		super(task);
	}

	public abstract int getDuration();

	public @NotNull String getBossBarTitle(Match match, int elapsed) {
		int duration = getDuration();
		StringBuilder title = new StringBuilder(task.getName());
		if (duration > 0)
			title.append(" in ").append(duration - elapsed).append('s');
		return title.toString();
	}
}
