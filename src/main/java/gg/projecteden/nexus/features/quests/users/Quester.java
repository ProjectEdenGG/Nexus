package gg.projecteden.nexus.features.quests.users;

import gg.projecteden.nexus.features.quests.interactable.Interactable;
import gg.projecteden.nexus.features.quests.interactable.instructions.DialogInstance;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTaskStep;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.PlayerUtils;
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

import static gg.projecteden.utils.Nullables.isNullOrEmpty;

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
		if (dialog != null && dialog.getTaskId().get() > 0) {
			dialog.advance();
			return;
		}

		for (Quest quest : new ArrayList<>(quests)) {
			final QuestTaskProgress questTask = quest.getCurrentTaskProgress();
			final QuestTaskStepProgress step = questTask.currentStep();
			final QuestTaskStep<?, ?> taskStep = questTask.get().getSteps().get(questTask.getStep());

			if (taskStep.getInteractable() == interactable) {
				dialog = taskStep.interact(this, step);

				if (taskStep.shouldAdvance(this, step)) {
					if (questTask.hasNextStep())
						questTask.incrementStep();
					else {
						questTask.reward();
						if (quest.hasNextTask())
							quest.incrementTask();
						else
							quest.complete();
					}
				}

				step.setFirstInteraction(false);
				return;
			}
		}

		for (Quest quest : quests) {
			final QuestTaskProgress questTask = quest.getCurrentTaskProgress();
			if (!questTask.hasPreviousStep())
				continue;

			final QuestTaskStepProgress step = questTask.previousStep();
			final QuestTaskStep<?, ?> taskStep = questTask.get().getSteps().get(questTask.getStep() - 1);

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

	public void remove(List<ItemStack> items) {
		if (!isNullOrEmpty(items))
			for (ItemStack item : items)
				PlayerUtils.removeItem(getOnlinePlayer(), item);
	}

}
