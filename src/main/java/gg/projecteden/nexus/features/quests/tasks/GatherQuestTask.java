package gg.projecteden.nexus.features.quests.tasks;

import gg.projecteden.nexus.features.quests.QuestItem;
import gg.projecteden.nexus.features.quests.interactable.instructions.Dialog;
import gg.projecteden.nexus.features.quests.interactable.instructions.DialogInstance;
import gg.projecteden.nexus.features.quests.tasks.GatherQuestTask.GatherQuestTaskStep;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTaskStep;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.quests.QuestTaskStepProgress;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

@Data
public class GatherQuestTask extends QuestTask<GatherQuestTask, GatherQuestTaskStep> {

	public GatherQuestTask(List<GatherQuestTaskStep> steps) {
		super(steps);
	}

	@Data
	public static class GatherQuestTaskStep extends QuestTaskStep<GatherQuestTask, GatherQuestTaskStep> {
		private Predicate<Quester> questerPredicate;
		private Predicate<ItemStack> itemPredicate;
		private int amount;
		private List<ItemStack> items = new ArrayList<>();
		private boolean take = true;
		private Dialog complete;

		@Override
		public DialogInstance interact(Quester quester, QuestTaskStepProgress stepProgress) {
			if (dialog != null && stepProgress.isFirstInteraction()) {
				return dialog.send(quester);
			} else if (shouldAdvance(quester, stepProgress)) {
				if (complete != null)
					return complete.send(quester);
			} else if (reminder != null)
				return reminder.send(quester);

			return null;
		}

		@Override
		public boolean shouldAdvance(Quester quester, QuestTaskStepProgress stepProgress) {
			if (questerPredicate != null) {
				if (!stepProgress.isFirstInteraction() && questerPredicate.test(quester))
					return true;
			} else if (itemPredicate != null && amount > 0) {
				if (!stepProgress.isFirstInteraction() && quester.has(itemPredicate, amount))
					return true;
			} else {
				if (Nullables.isNullOrEmpty(items))
					return true;

				if (!stepProgress.isFirstInteraction() && quester.has(items))
					return true;
			}

			return false;
		}

		@Override
		public void afterComplete(Quester quester) {
			if (take)
				if (itemPredicate != null)
					quester.take(itemPredicate, amount);
				else
					quester.take(items);
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

		public GatherTaskBuilder gather(Map<Material, Integer> items) {
			return gather(items.entrySet().stream().map(entry -> new ItemStack(entry.getKey(), entry.getValue())).toList());
		}

		public GatherTaskBuilder gather(Material material, int amount) {
			return gather(new ItemStack(material, amount));
		}

		public GatherTaskBuilder gather(ItemModelType itemModelType, int amount) {
			return gather(itemModelType.getNamedItem(), amount);
		}

		public GatherTaskBuilder gather(ItemStack... items) {
			return gather(List.of(items));
		}

		public GatherTaskBuilder gather(QuestItem... items) {
			for (QuestItem item : items)
				gather(item.get());

			return this;
		}

		public GatherTaskBuilder gather(QuestItem item, int amount) {
			return gather(item.get(), amount);
		}

		public GatherTaskBuilder gather(ItemStack item, int amount) {
			return gather(new ItemBuilder(item).amount(amount).build());
		}

		public GatherTaskBuilder gather(List<ItemStack> items) {
			currentStep.items.addAll(items);
			return this;
		}

		public GatherTaskBuilder gather(MaterialTag materials, int amount) {
			return gather(materials::isTagged, amount);
		}

		public GatherTaskBuilder gather(Predicate<ItemStack> predicate, int amount) {
			currentStep.itemPredicate = predicate;
			currentStep.amount = amount;
			return this;
		}

		public GatherTaskBuilder check(Predicate<Quester> predicate) {
			currentStep.questerPredicate = predicate;
			return this;
		}

		public GatherTaskBuilder take(boolean take) {
			currentStep.take = take;
			return this;
		}

		public GatherTaskBuilder reminder(BiFunction<Dialog, List<ItemStack>, Dialog> reminder) {
			currentStep.reminder = reminder.apply(Dialog.from(currentStep.interactable), currentStep.items);
			return this;
		}

		public GatherTaskBuilder complete(Function<Dialog, Dialog> complete) {
			currentStep.complete = complete.apply(Dialog.from(currentStep.getInteractable()));
			return this;
		}

	}

}
