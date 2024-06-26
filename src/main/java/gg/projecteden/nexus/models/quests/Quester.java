package gg.projecteden.nexus.models.quests;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.quests.interactable.Interactable;
import gg.projecteden.nexus.features.quests.interactable.InteractableEntity;
import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import gg.projecteden.nexus.features.quests.interactable.instructions.Dialog;
import gg.projecteden.nexus.features.quests.interactable.instructions.DialogInstance;
import gg.projecteden.nexus.features.quests.tasks.common.IQuest;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTaskStep;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.citizensnpcs.api.event.NPCClickEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

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

	public Quest getQuest(IQuest quest) {
		return quests.stream()
			.filter(startedQuest -> startedQuest.getQuest() == quest)
			.findFirst()
			.orElse(null);
	}

	public boolean hasStarted(IQuest quest) {
		return getQuest(quest) != null;
	}

	public void interact(PlayerInteractEvent event) {
		final Block block = event.getClickedBlock();
		if (isNullOrAir(block))
			return;

		for (Quest quest : quests) {
			if (quest.isComplete())
				continue;

			final QuestTaskProgress taskProgress = quest.getCurrentTaskProgress();
			final QuestTaskStep<?, ?> taskStep = taskProgress.get().getSteps().get(taskProgress.getStep());

			for (var pair : taskStep.getOnBlockInteract().keySet()) {
				if (!pair.getFirst().contains(block.getType()))
					continue;

				if (!pair.getSecond().contains(event.getAction()))
					continue;

				taskStep.getOnBlockInteract().get(pair).accept(event, block);
			}
		}
	}

	public <E extends Event> void interact(Interactable interactable, E event) {
		if (dialog != null && dialog.getTaskId().get() > 0) {
			dialog.advance();
			return;
		}

		for (Quest quest : quests) {
			if (quest.isComplete())
				continue;

			final QuestTaskProgress taskProgress = quest.getCurrentTaskProgress();
			final QuestTaskStepProgress stepProgress = taskProgress.currentStep();
			final QuestTaskStep<?, ?> taskStep = taskProgress.get().getSteps().get(taskProgress.getStep());

			if (taskStep.getInteractable() == interactable) {
				dialog = taskStep.interact(this, stepProgress);

				if (taskStep.shouldAdvance(this, stepProgress)) {
					taskStep.afterComplete(this);

					if (taskProgress.hasNextStep()) {
						taskProgress.incrementStep();
					} else {
						taskProgress.reward();
						if (quest.hasNextTask())
							quest.incrementTask();
						else
							quest.complete();
					}
				}

				stepProgress.setFirstInteraction(false);

				return;
			} else if (taskStep.getOnClick().containsKey(interactable)) {
				dialog = taskStep.getOnClick().get(interactable).send(this);
				return;
			} else if (interactable instanceof InteractableNPC) {
				if (event instanceof NPCClickEvent castedEvent) {
					if (taskStep.getOnNPCInteract().containsKey(interactable)) {
						taskStep.getOnNPCInteract().get(interactable).accept(castedEvent);
						return;
					}
				}
			} else if (interactable instanceof InteractableEntity) {
				if (event instanceof PlayerInteractEntityEvent castedEvent) {
					if (taskStep.getOnEntityInteract().containsKey(interactable)) {
						taskStep.getOnEntityInteract().get(interactable).accept(castedEvent);
						return;
					}
				}
			}
		}

		// TODO Look for quests to start

		if (interactable.isAlive())
			Dialog.genericGreeting(this, interactable);
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

	public boolean has(MaterialTag materials, int amount) {
		int found = 0;

		for (ItemStack content : getOnlinePlayer().getInventory().getContents()) {
			if (isNullOrAir(content))
				continue;

			if (materials.isTagged(content.getType())) {
				found += content.getAmount();
			}
		}

		return found >= amount;
	}

	public void remove(List<ItemStack> items) {
		if (!isNullOrEmpty(items))
			for (ItemStack item : items)
				PlayerUtils.removeItem(getOnlinePlayer(), item);
	}

}
