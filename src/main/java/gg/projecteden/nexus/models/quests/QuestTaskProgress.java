package gg.projecteden.nexus.models.quests;

import dev.morphia.annotations.Converters;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTask;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.EnumInterfaceConverter;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, EnumInterfaceConverter.class})
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
		return QuestTask.CACHE.computeIfAbsent(task, $ -> task.builder().build());
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
		++step;
	}

	public void reward() {
		get().getRewards().forEach(consumer -> consumer.accept(quester()));
	}

}
