package me.pugabyte.nexus.features.events.models;

import me.pugabyte.nexus.models.pugmas20.Pugmas20User;

import java.util.Map;
import java.util.function.Function;

public interface Quest {

	Function<Pugmas20User, Map<QuestStage, String>> getInstructions();

	default String getInstructions(Pugmas20User user, QuestStage stage) {
		return getInstructions().apply(user).getOrDefault(stage, null);
	}

}
