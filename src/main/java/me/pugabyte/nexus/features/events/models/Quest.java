package me.pugabyte.nexus.features.events.models;

import java.util.Map;

public interface Quest {

	Map<QuestStage, String> getInstructions();

	default String getInstructions(QuestStage stage) {
		return getInstructions().getOrDefault(stage, null);
	}

}
