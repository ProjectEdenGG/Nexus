package gg.projecteden.nexus.features.quests.tasks;

import gg.projecteden.nexus.features.quests.interactable.instructions.Dialog;
import gg.projecteden.nexus.features.quests.interactable.instructions.DialogInstance;
import gg.projecteden.nexus.features.quests.tasks.GatherQuestTask.GatherQuestTaskStep;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTaskStep;
import gg.projecteden.nexus.features.quests.users.QuestTaskStepProgress;
import gg.projecteden.nexus.features.quests.users.Quester;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

import static gg.projecteden.nexus.features.quests.interactable.instructions.Dialog.genericGreeting;
import static java.util.Collections.singletonList;

@Data
public class GatherQuestTask extends QuestTask<GatherQuestTask, GatherQuestTaskStep> {

	public GatherQuestTask(List<GatherQuestTaskStep> steps) {
		super(steps);
	}

	public static class GatherQuestTaskStep extends QuestTaskStep<GatherQuestTask, GatherQuestTaskStep> {
		private List<ItemStack> items;
		private Dialog complete;

		@Override
		public DialogInstance interact(Quester quester, QuestTaskStepProgress stepProgress) {
			if (stepProgress.isFirstInteraction())
				return dialog.send(quester);
			else
				if (shouldAdvance(quester, stepProgress))
					return complete.send(quester);
				else
					if (reminder != null)
						return reminder.send(quester);
					else
						return genericGreeting(quester, interactable);
		}

		@Override
		public boolean shouldAdvance(Quester quester, QuestTaskStepProgress stepProgress) {
			return !stepProgress.isFirstInteraction() && quester.has(items);
		}

	}

	public static GatherTaskBuilder builder() {
		return new GatherTaskBuilder();
	}

	@NoArgsConstructor
	public static class GatherTaskBuilder extends TaskBuilder<GatherQuestTask, GatherTaskBuilder, GatherQuestTaskStep> {

		@Override
		public GatherQuestTaskStep nextStep() {
			return new GatherQuestTaskStep();
		}

		@NotNull
		public GatherQuestTask newInstance() {
			return new GatherQuestTask(steps);
		}

		public GatherTaskBuilder gather(ItemStack item) {
			return gather(singletonList(item));
		}

		public GatherTaskBuilder gather(List<ItemStack> items) {
			currentStep.items = items;
			return this;
		}

		public GatherTaskBuilder complete(Function<Dialog, Dialog> complete) {
			currentStep.complete = complete.apply(Dialog.from(currentStep.getInteractable()));
			return this;
		}

	}

}
