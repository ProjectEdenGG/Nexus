package gg.projecteden.nexus.models.quests;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.listeners.events.LivingEntityKilledByPlayerEvent;
import gg.projecteden.nexus.features.quests.interactable.Interactable;
import gg.projecteden.nexus.features.quests.interactable.InteractableEntity;
import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import gg.projecteden.nexus.features.quests.interactable.instructions.Dialog;
import gg.projecteden.nexus.features.quests.interactable.instructions.DialogInstance;
import gg.projecteden.nexus.features.quests.tasks.common.IQuest;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTaskStep;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.citizensnpcs.api.event.NPCClickEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Predicate;

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

	public static Quester of(HasUniqueId player) {
		return of(player.getUniqueId());
	}

	public static Quester of(UUID uuid) {
		return new QuesterService().get(uuid);
	}

	public boolean tryAdvanceDialog(Interactable interactable) {
		if (dialog != null && dialog.getTaskId().get() > 0) {
			if (dialog.getDialog().getInteractable().equals(interactable))
				dialog.advance();
			return true;
		}

		return false;
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

	public boolean hasCompleted(IQuest quest) {
		return hasStarted(quest) && getQuest(quest).isComplete();
	}

	public void interact(PlayerInteractEvent event) {
		final Block block = event.getClickedBlock();
		if (Nullables.isNullOrAir(block))
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

	public void handleBlockEvent(BlockEvent event) {
		for (Quest quest : quests) {
			if (quest.isComplete())
				continue;

			final QuestTaskProgress taskProgress = quest.getCurrentTaskProgress();
			final QuestTaskStep<?, ?> taskStep = taskProgress.get().getSteps().get(taskProgress.getStep());

			if (event instanceof BlockDropItemEvent blockDropItemEvent) {
				if (blockDropItemEvent.getItems().stream().map(Item::getItemStack).anyMatch(Nullables::isNotNullOrAir)) {
					for (var materials : taskStep.getOnBlockDropItem().keySet()) {
						if (!materials.contains(event.getBlock().getType()))
							continue;

						taskStep.getOnBlockDropItem().get(materials).accept(blockDropItemEvent, event.getBlock());
					}
				}
			}
		}
	}

	public void handleEntityEvent(EntityEvent event) {
		for (Quest quest : quests) {
			if (quest.isComplete())
				continue;

			final QuestTaskProgress taskProgress = quest.getCurrentTaskProgress();
			final QuestTaskStep<?, ?> taskStep = taskProgress.get().getSteps().get(taskProgress.getStep());

			if (event instanceof LivingEntityKilledByPlayerEvent killEvent) {
				for (var entityClass : taskStep.getOnLivingEntityKilledByPlayer().keySet()) {
					if (!entityClass.isAssignableFrom(event.getEntity().getClass()))
						continue;

					taskStep.getOnLivingEntityKilledByPlayer().get(entityClass).accept(killEvent, killEvent.getEntity());
				}
			}
		}
	}

	public <E extends Event> void interact(Interactable interactable, E event) {
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

		final EdenEvent edenEvent = EdenEvent.of(getOnlinePlayer());
		if (edenEvent != null) {
			for (IQuest quest : edenEvent.getQuests()) {
				if (hasStarted(quest))
					continue;

				final Interactable firstInteractable = quest.getTasks().get(0).get().getSteps().get(0).getInteractable();
				if (!interactable.equals(firstInteractable))
					continue;

				quest.assign(this);
				interact(interactable, event);
				return;
			}
		}

		if (interactable.isAlive())
			Dialog.genericGreeting(this, interactable);
	}

	public boolean has(ItemStack item) {
		return has(Collections.singletonList(item));
	}

	public boolean has(List<ItemStack> items) {
		Map<ItemStack, Integer> amounts = new HashMap<>();

		for (ItemStack item : items)
			amounts.put(ItemBuilder.oneOf(item).build(), item.getAmount());

		for (ItemStack content : getOnlinePlayer().getInventory().getContents()) {
			if (Nullables.isNullOrAir(content))
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
		return has(item -> materials.isTagged(item.getType()), amount);
	}

	public boolean has(Predicate<ItemStack> predicate, int amount) {
		int found = 0;

		for (ItemStack content : getOnlinePlayer().getInventory().getContents()) {
			if (Nullables.isNullOrAir(content))
				continue;

			if (predicate.test(content)) {
				found += content.getAmount();
			}
		}

		return found >= amount;
	}

	public void take(ItemStack item) {
		take(Collections.singletonList(item));
	}

	public void take(List<ItemStack> items) {
		if (!gg.projecteden.api.common.utils.Nullables.isNullOrEmpty(items))
			for (ItemStack item : items)
				PlayerUtils.removeItem(getOnlinePlayer(), item);
	}

	public void take(Predicate<ItemStack> predicate, int amount) {
		int count = 0;
		for (ItemStack content : getOnlinePlayer().getInventory().getContents()) {
			if (Nullables.isNullOrAir(content))
				continue;

			if (predicate.test(content)) {
				var clone = content.clone();
				clone.setAmount(Math.min(amount - count, content.getAmount()));
				PlayerUtils.removeItem(getOnlinePlayer(), clone);

				count += clone.getAmount();
				if (count == amount)
					return;
			}
		}
	}

	public List<ItemStack> getRemainingItems(List<ItemStack> items) {
		List<ItemStack> remaining = new ArrayList<>();

		for (var item : items)
			if (!has(item))
				remaining.add(item);

		return remaining;
	}

	public List<String> getRemainingItemNames(List<ItemStack> items) {
		return getRemainingItems(items).stream().map(StringUtils::pretty).toList();
	}

}
