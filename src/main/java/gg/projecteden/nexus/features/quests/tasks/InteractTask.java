package gg.projecteden.nexus.features.quests.tasks;

import gg.projecteden.nexus.features.quests.interactable.instructions.DialogInstance;
import gg.projecteden.nexus.features.quests.tasks.InteractTask.InteractTaskStep;
import gg.projecteden.nexus.features.quests.tasks.common.Task;
import gg.projecteden.nexus.features.quests.users.QuestStepProgress;
import gg.projecteden.nexus.features.quests.users.Quester;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static gg.projecteden.nexus.features.quests.interactable.instructions.Dialog.genericGreeting;

@Data
public class InteractTask extends Task<InteractTask, InteractTaskStep> {

	public InteractTask(List<InteractTaskStep> steps) {
		super(steps);
	}

	public static class InteractTaskStep extends TaskStep<InteractTask, InteractTaskStep> {

		@Override
		public DialogInstance interact(Quester quester, QuestStepProgress stepProgress) {
			if (stepProgress.isFirstInteraction())
				return dialog.send(quester);
			else
				if (reminder != null)
					return reminder.send(quester);
				else
					return genericGreeting(quester, interactable);
		}

		@Override
		public boolean shouldAdvance(Quester quester, QuestStepProgress stepProgress) {
			return true;
		}

	}

	public static InteractTaskBuilder builder() {
		return new InteractTaskBuilder();
	}

	@NoArgsConstructor
	public static class InteractTaskBuilder extends TaskBuilder<InteractTask, InteractTaskBuilder, InteractTaskStep> {

		@Override
		public InteractTaskStep nextStep() {
			return new InteractTaskStep();
		}

		@Override
		public InteractTask newInstance() {
			return new InteractTask(steps);
		}

	}
}
