package gg.projecteden.nexus.features.quests.tasks.common;

import gg.projecteden.nexus.features.listeners.events.LivingEntityKilledByPlayerEvent;
import gg.projecteden.nexus.features.quests.QuestReward;
import gg.projecteden.nexus.features.quests.interactable.Interactable;
import gg.projecteden.nexus.features.quests.interactable.InteractableEntity;
import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import gg.projecteden.nexus.features.quests.interactable.instructions.Dialog;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteringRegionEvent;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import kotlin.Pair;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.citizensnpcs.api.event.NPCClickEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Data
@NoArgsConstructor
public abstract class QuestTask<
	TaskType extends QuestTask<TaskType, TaskStepType>,
	TaskStepType extends QuestTaskStep<TaskType, TaskStepType>
> {
	protected List<TaskStepType> steps;
	protected List<Consumer<Quester>> rewards = new ArrayList<>();

	public static Map<IQuestTask, QuestTask<?, ?>> CACHE = new ConcurrentHashMap<>();

	public QuestTask(List<TaskStepType> steps) {
		this.steps = steps;
	}

	public static abstract class TaskBuilder<
		TaskType extends QuestTask<TaskType, TaskStepType>,
		TaskBuilderType extends TaskBuilder<TaskType, TaskBuilderType, TaskStepType>,
		TaskStepType extends QuestTaskStep<TaskType, TaskStepType>
	> {
		protected List<TaskStepType> steps = new ArrayList<>();
		protected TaskStepType currentStep = nextStep();
		protected List<Consumer<Quester>> rewards = new ArrayList<>();

		abstract public TaskStepType nextStep();

		abstract public TaskType newInstance();

		public TaskType build() {
			then();
			return newInstance();
		}

		public TaskBuilderType talkTo(Interactable interactable) {
			currentStep.interactable = interactable;
			return (TaskBuilderType) this;
		}

		public TaskBuilderType objective(String message) {
			return objective(new JsonBuilder(message));
		}

		public TaskBuilderType objective(JsonBuilder message) {
			currentStep.objective = message;
			return (TaskBuilderType) this;
		}

		public TaskBuilderType dialog(Function<Dialog, Dialog> instructions) {
			currentStep.dialog = instructions.apply(Dialog.from(currentStep.interactable));
			return (TaskBuilderType) this;
		}

		public TaskBuilderType reminder(Function<Dialog, Dialog> reminder) {
			currentStep.reminder = reminder.apply(Dialog.from(currentStep.interactable));
			return (TaskBuilderType) this;
		}

		public TaskBuilderType reward(QuestReward reward) {
			rewards.add(reward::apply);
			return (TaskBuilderType) this;
		}

		public TaskBuilderType reward(QuestReward reward, int amount) {
			rewards.add(quester -> reward.apply(quester, amount));
			return (TaskBuilderType) this;
		}

		public TaskBuilderType onClick(Interactable interactable, Function<Dialog, Dialog> instructions) {
			currentStep.onClick.computeIfAbsent(interactable, $ -> instructions.apply(Dialog.from(interactable)));
			return (TaskBuilderType) this;
		}

		public TaskBuilderType onNPCInteract(InteractableNPC interactable, Consumer<NPCClickEvent> consumer) {
			currentStep.onNPCInteract.computeIfAbsent(interactable, $ -> consumer);
			return (TaskBuilderType) this;
		}

		public TaskBuilderType onEntityInteract(InteractableEntity interactable, Consumer<PlayerInteractEntityEvent> consumer) {
			currentStep.onEntityInteract.computeIfAbsent(interactable, $ -> consumer);
			return (TaskBuilderType) this;
		}

		public TaskBuilderType onRegionEntering(String regionId, Consumer<PlayerEnteringRegionEvent> consumer) {
			currentStep.onRegionEntering.computeIfAbsent(regionId, $ -> consumer);
			return (TaskBuilderType) this;
		}

		public TaskBuilderType onBlockInteract(Material material, Action action, BiConsumer<PlayerInteractEvent, Block> event) {
			return onBlockInteract(List.of(material), List.of(action), event);
		}

		public TaskBuilderType onBlockInteract(MaterialTag materials, Action action, BiConsumer<PlayerInteractEvent, Block> event) {
			return onBlockInteract(materials.getValues().stream().toList(), List.of(action), event);
		}

		public TaskBuilderType onBlockInteract(List<Material> materials, Action action, BiConsumer<PlayerInteractEvent, Block> event) {
			return onBlockInteract(materials, List.of(action), event);
		}

		public TaskBuilderType onBlockInteract(Material material, List<Action> action, BiConsumer<PlayerInteractEvent, Block> event) {
			return onBlockInteract(List.of(material), action, event);
		}

		public TaskBuilderType onBlockInteract(List<Material> materials, List<Action> actions, BiConsumer<PlayerInteractEvent, Block> event) {
			currentStep.onBlockInteract.put(new Pair<>(materials, actions), event);
			return (TaskBuilderType) this;
		}

		public TaskBuilderType onBlockDropItem(MaterialTag materials, BiConsumer<BlockDropItemEvent, Block> event) {
			currentStep.onBlockDropItem.put(materials.getValues().stream().toList(), event);
			return (TaskBuilderType) this;
		}

		public <T extends LivingEntity> TaskBuilderType onLivingEntityKilledByPlayer(Class<T> entityType, BiConsumer<LivingEntityKilledByPlayerEvent, T> event) {
			currentStep.onLivingEntityKilledByPlayer.put(entityType, (BiConsumer<LivingEntityKilledByPlayerEvent, LivingEntity>) event);
			return (TaskBuilderType) this;
		}

		public TaskBuilderType then() {
			steps.add(currentStep);
			currentStep = nextStep();
			return (TaskBuilderType) this;
		}

	}

}
