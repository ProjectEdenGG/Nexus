package gg.projecteden.nexus.features.quests.tasks.common;

import gg.projecteden.nexus.features.quests.interactable.Interactable;
import gg.projecteden.nexus.features.quests.interactable.instructions.Dialog;
import gg.projecteden.nexus.features.quests.interactable.instructions.DialogInstance;
import gg.projecteden.nexus.models.quests.QuestTaskStepProgress;
import gg.projecteden.nexus.models.quests.Quester;
import lombok.Data;

@Data
public abstract class QuestTaskStep<
	TaskType extends QuestTask<TaskType, TaskStepType>,
	TaskStepType extends QuestTaskStep<TaskType, TaskStepType>
> {
	protected Interactable interactable;
	protected Dialog dialog;
	protected Dialog reminder;

	abstract public DialogInstance interact(Quester quester, QuestTaskStepProgress stepProgress);

	abstract public boolean shouldAdvance(Quester quester, QuestTaskStepProgress stepProgress);

	public void afterComplete(Quester quester) {}

}
