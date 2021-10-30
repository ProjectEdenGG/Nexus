package gg.projecteden.nexus.features.quests.users;

import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTask;
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
	private IQuestTask task;
	private Map<Integer, QuestTaskStepProgress> steps = new HashMap<>();
	private int step;

	public Quester quester() {
		return Quester.of(uuid);
	}

	@ToString.Include
	public QuestTask<?, ?> get() {
		return task.get();
	}

	public boolean hasPreviousStep() {
		return step > 0;
	}

	public QuestTaskStepProgress previousStep() {
		return steps.computeIfAbsent(step - 1, $ -> new QuestTaskStepProgress(uuid));
	}

	public QuestTaskStepProgress currentStep() {
		return steps.computeIfAbsent(step, $ -> new QuestTaskStepProgress(uuid));
	}

	public boolean hasNextStep() {
		return get().getSteps().size() > step + 1;
	}

	public QuestTaskStepProgress nextStep() {
		return steps.computeIfAbsent(step + 1, $ -> new QuestTaskStepProgress(uuid));
	}

	public void incrementStep() {
		sendMessage("&c=== Moving to next step");
		++step;
	}

	public void reward() {
		get().getRewards().forEach(consumer -> consumer.accept(quester()));
	}

}
