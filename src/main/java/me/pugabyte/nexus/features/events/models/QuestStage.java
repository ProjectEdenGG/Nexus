package me.pugabyte.nexus.features.events.models;

public enum QuestStage {
	INELIGIBLE,
	NOT_STARTED,
	STARTED,
	STEP_ONE,
	STEP_TWO,
	STEP_THREE,
	STEP_FOUR,
	STEP_FIVE,
	STEPS_DONE,
	FOUND_ALL,
	COMPLETE;

	public boolean isInProgress() {
		return switch (this) {
			case INELIGIBLE, NOT_STARTED, COMPLETE -> false;
			default -> true;
		};
	}

	public boolean isComplete() {
		return this == COMPLETE;
	}

	public boolean canStart() {
		return this != INELIGIBLE;
	}
}
