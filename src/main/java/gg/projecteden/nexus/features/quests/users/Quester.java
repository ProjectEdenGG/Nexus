package gg.projecteden.nexus.features.quests.users;

import gg.projecteden.nexus.features.quests.interactable.Interactable;
import gg.projecteden.nexus.features.quests.interactable.instructions.DialogInstance;
import gg.projecteden.nexus.features.quests.tasks.common.Task.TaskStep;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class Quester implements PlayerOwnedObject {
	@NonNull
	private UUID uuid;
	private List<Quest> quests = new ArrayList<>();

	private DialogInstance dialog;

	// Temporary
	private static final Map<UUID, Quester> cache = new HashMap<>();

	public static Quester of(Player player) {
		return of(player.getUniqueId());
	}

	public static Quester of(UUID uuid) {
		return cache.computeIfAbsent(uuid, $ -> new Quester(uuid));
	}
	//

	public void interact(Interactable interactable) {
		System.out.println("checking dialog");
		if (dialog != null && dialog.getTaskId().get() > 0) {
			dialog.advance();
			return;
		}

		System.out.println("checking quests @ current step");
		for (Quest quest : new ArrayList<>(quests)) {
			System.out.println(quest);
			final QuestTaskProgress questTask = quest.getCurrentTaskProgress();
			final QuestStepProgress step = questTask.currentStep();
			final TaskStep<?, ?> taskStep = questTask.get().getSteps().get(questTask.getStep());

			if (taskStep.getInteractable() == interactable) {
				dialog = taskStep.interact(this, step);

				if (taskStep.shouldAdvance(this, step))
					questTask.incrementStep();

				if (questTask.get().getSteps().size() <= questTask.getStep())
					quest.incrementTask();

				if (quest.getTasks().size() <= quest.getTask())
					quest.complete();

				step.setFirstInteraction(false);
				return;
			}
		}

		System.out.println("checking quests @ previous step");
		for (Quest quest : quests) {
			final QuestTaskProgress questTask = quest.getCurrentTaskProgress();
			if (!questTask.hasPreviousStep())
				continue;

			final QuestStepProgress step = questTask.previousStep();
			final TaskStep<?, ?> taskStep = questTask.get().getSteps().get(questTask.getStep() - 1);

			if (taskStep.getInteractable() == interactable) {
				dialog = taskStep.interact(this, step);
				step.setFirstInteraction(false);
				return;
			}
		}

		// TODO Look for quests to start
	}

	public boolean has(List<ItemStack> items) {
		// TODO
		return true;
	}

}
