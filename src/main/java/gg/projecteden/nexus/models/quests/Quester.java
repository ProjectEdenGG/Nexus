package gg.projecteden.nexus.models.quests;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.quests.interactable.Interactable;
import gg.projecteden.nexus.features.quests.interactable.InteractableEntity;
import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import gg.projecteden.nexus.features.quests.interactable.instructions.Dialog;
import gg.projecteden.nexus.features.quests.interactable.instructions.DialogInstance;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTaskStep;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.ItemBuilder;
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

	public void interact(PlayerInteractEvent event) {
		final Block block = event.getClickedBlock();
		if (isNullOrAir(block))
			return;

		for (Quest quest : quests) {
			final QuestTaskProgress questTask = quest.getTaskProgress();
			final QuestTaskStep<?, ?> taskStep = questTask.get().getSteps().get(questTask.getStep());

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
			final QuestTaskProgress questTask = quest.getTaskProgress();
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

	public void remove(List<ItemStack> items) {
		if (!isNullOrEmpty(items))
			for (ItemStack item : items)
				PlayerUtils.removeItem(getOnlinePlayer(), item);
	}

}
