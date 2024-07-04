package gg.projecteden.nexus.features.quests.tasks;

import gg.projecteden.nexus.features.quests.interactable.instructions.DialogInstance;
import gg.projecteden.nexus.features.quests.tasks.InteractQuestTask.InteractQuestTaskStep;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTaskStep;
import gg.projecteden.nexus.models.quests.QuestTaskStepProgress;
import gg.projecteden.nexus.models.quests.Quester;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class InteractQuestTask extends QuestTask<InteractQuestTask, InteractQuestTaskStep> {

	public InteractQuestTask(List<InteractQuestTaskStep> steps) {
		super(steps);
	}

	@Data
	public static class InteractQuestTaskStep extends QuestTaskStep<InteractQuestTask, InteractQuestTaskStep> {

		@Override
		public DialogInstance interact(Quester quester, QuestTaskStepProgress stepProgress) {
			if (dialog != null && stepProgress.isFirstInteraction())
				return dialog.send(quester);
			else
				if (reminder != null)
					return reminder.send(quester);
				else
					return null;
		}

		@Override
		public boolean shouldAdvance(Quester quester, QuestTaskStepProgress stepProgress) {
			return true;
		}

	}

	public static InteractTaskBuilder builder() {
		return new InteractTaskBuilder();
	}

	@NoArgsConstructor
	public static class InteractTaskBuilder extends TaskBuilder<InteractQuestTask, InteractTaskBuilder, InteractQuestTaskStep> {

		@Override
		public InteractQuestTaskStep nextStep() {
			return new InteractQuestTaskStep();
		}

		@Override
		public InteractQuestTask newInstance() {
			return new InteractQuestTask(steps);
		}

	}
}
