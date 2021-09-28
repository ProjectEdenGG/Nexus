package gg.projecteden.nexus.features.quests.users;

import gg.projecteden.nexus.features.quests.tasks.common.ITask;
import gg.projecteden.nexus.features.quests.tasks.common.Task;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class QuestTaskProgress implements PlayerOwnedObject {
	@NonNull
	private UUID uuid;
	@NonNull
	private ITask task;
	private Map<Integer, QuestStepProgress> steps = new HashMap<>();
	private int step;

	@ToString.Include
	public Task<?, ?> get() {
		return task.get();
	}

	public boolean hasPreviousStep() {
		return step > 0;
	}

	public QuestStepProgress previousStep() {
		return steps.computeIfAbsent(step - 1, $ -> new QuestStepProgress(uuid));
	}

	public QuestStepProgress currentStep() {
		return steps.computeIfAbsent(step, $ -> new QuestStepProgress(uuid));
	}

	public boolean hasNextStep() {
		return get().getSteps().size() > step;
	}

	public QuestStepProgress nextStep() {
		return steps.computeIfAbsent(step + 1, $ -> new QuestStepProgress(uuid));
	}

	public void incrementStep() {
		sendMessage("Moving to next step");
		++step;
	}

}
