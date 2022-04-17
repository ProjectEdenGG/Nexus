package gg.projecteden.nexus.models.quests;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.quests.interactable.Interactable;
import gg.projecteden.nexus.features.quests.interactable.instructions.DialogInstance;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTaskStep;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.utils.Nullables.isNullOrEmpty;

@Data
@Entity(value = "quester", noClassnameStored = true)
@NoArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class Quester implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<Quest> quests = new ArrayList<>();

	private transient DialogInstance dialog;

	public static Quester of(Player player) {
		return of(player.getUniqueId());
	}

	public static Quester of(UUID uuid) {
		return new QuesterService().get(uuid);
	}

	public boolean interact(Interactable interactable) {
		if (dialog != null && dialog.getTaskId().get() > 0) {
			dialog.advance();
			return true;
		}

		for (Quest quest : new ArrayList<>(quests)) {
			final QuestTaskProgress questTask = quest.getCurrentTaskProgress();
			final QuestTaskStepProgress step = questTask.currentStep();
			final QuestTaskStep<?, ?> taskStep = questTask.get().getSteps().get(questTask.getStep());

			if (taskStep.getInteractable() == interactable) {
				dialog = taskStep.interact(this, step);

				if (taskStep.shouldAdvance(this, step)) {
					taskStep.afterComplete(this);

					if (questTask.hasNextStep())
						questTask.incrementStep();
					else {
						questTask.reward();
						if (quest.hasNextTask())
							quest.incrementTask();
						else
							quest.isComplete();
					}
				}

				step.setFirstInteraction(false);

				return true;
			} else if (taskStep.getOnClick().containsKey(interactable)) {
				dialog = taskStep.getOnClick().get(interactable).send(this);
				return true;
			}
		}

		/*
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
		*/

		// TODO Look for quests to start

		return false;
	}

	public boolean has(List<ItemStack> items) {
		Map<ItemStack, Integer> amounts = new HashMap<>();

		for (ItemStack item : items)
			amounts.put(ItemBuilder.oneOf(item).build(), item.getAmount());

		for (ItemStack content : getOnlinePlayer().getInventory().getContents()) {
			if (isNullOrAir(content))
				continue;

			for (ItemStack item : items)
				if (content.isSimilar(item)) {
					final Optional<ItemStack> match = amounts.keySet().stream().filter(key -> key.isSimilar(item)).findFirst();

					if (match.isPresent()) {
						int left = amounts.getOrDefault(match.get(), 0) - content.getAmount();
						if (left <= 0)
							amounts.remove(match.get());
						else
							amounts.put(match.get(), left);
					}
				}
		}

		return amounts.isEmpty();
	}

	public void remove(List<ItemStack> items) {
		if (!isNullOrEmpty(items))
			for (ItemStack item : items)
				PlayerUtils.removeItem(getOnlinePlayer(), item);
	}

}
