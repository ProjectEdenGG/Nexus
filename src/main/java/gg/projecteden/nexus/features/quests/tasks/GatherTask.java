package gg.projecteden.nexus.features.quests.tasks;

import gg.projecteden.nexus.features.quests.interactable.instructions.Dialog;
import gg.projecteden.nexus.features.quests.interactable.instructions.DialogInstance;
import gg.projecteden.nexus.features.quests.tasks.GatherTask.GatherTaskStep;
import gg.projecteden.nexus.features.quests.tasks.common.Task;
import gg.projecteden.nexus.features.quests.users.QuestStepProgress;
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
public class GatherTask extends Task<GatherTask, GatherTaskStep> {

	public GatherTask(List<GatherTaskStep> steps) {
		super(steps);
	}

	public static class GatherTaskStep extends TaskStep<GatherTask, GatherTaskStep> {
		private List<ItemStack> items;
		private Dialog complete;

		@Override
		public DialogInstance interact(Quester quester, QuestStepProgress stepProgress) {
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
		public boolean shouldAdvance(Quester quester, QuestStepProgress stepProgress) {
			return !stepProgress.isFirstInteraction() && quester.has(items);
		}

	}

	public static GatherTaskBuilder builder() {
		return new GatherTaskBuilder();
	}

	@NoArgsConstructor
	public static class GatherTaskBuilder extends TaskBuilder<GatherTask, GatherTaskBuilder, GatherTaskStep> {

		@Override
		public GatherTaskStep nextStep() {
			return new GatherTaskStep();
		}

		@NotNull
		public GatherTask newInstance() {
			return new GatherTask(steps);
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
