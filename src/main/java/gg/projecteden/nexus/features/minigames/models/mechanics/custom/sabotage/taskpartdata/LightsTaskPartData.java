package gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.taskpartdata;

import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.TaskPart;
import lombok.Getter;

public class LightsTaskPartData extends SabotageTaskPartData {
	@Getter
	private final boolean[] switches = {false, false, false, false, false};

	public LightsTaskPartData(TaskPart task) {
		super(task);
	}

	@Override
	public int getDuration() {
		return 0;
	}

	/**
	 * Toggles a light switch at the index
	 * @param index switch index
	 * @return if the sabotage is now resolved
	 */
	public boolean toggle(int index) {
		switches[index] = !switches[index];
		for (boolean swtch : switches)
			if (!swtch)
				return false;
		return true;
	}

	@Override
	public boolean hasRunnable() {
		return false;
	}
}
