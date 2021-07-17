package me.pugabyte.nexus.features.minigames.models.mechanics.custom.sabotage.taskpartdata;

import me.pugabyte.nexus.features.minigames.models.mechanics.custom.sabotage.TaskPart;

import java.util.Random;

public class DivertPower1TaskPartData extends TaskPartData {
	private static final Random RANDOM = new Random();
	private final int lever = RANDOM.nextInt(9);

	public DivertPower1TaskPartData(TaskPart task) {
		super(task);
	}

	/**
	 * Returns the column/position of the correct switch/lever
	 * @return lever column
	 */
	public int getLever() {
		return lever;
	}
}
