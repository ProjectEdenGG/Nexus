package gg.projecteden.nexus.features.quests.tasks.common;

import gg.projecteden.nexus.features.quests.interactable.Interactable;
import gg.projecteden.nexus.features.quests.interactable.InteractableEntity;
import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import gg.projecteden.nexus.features.quests.interactable.instructions.Dialog;
import gg.projecteden.nexus.features.quests.interactable.instructions.DialogInstance;
import gg.projecteden.nexus.models.quests.QuestTaskStepProgress;
import gg.projecteden.nexus.models.quests.Quester;
import lombok.Data;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Data
public abstract class QuestTaskStep<
	TaskType extends QuestTask<TaskType, TaskStepType>,
	TaskStepType extends QuestTaskStep<TaskType, TaskStepType>
> {
	protected Interactable interactable;
	protected ComponentLike objective;
	protected Dialog dialog;
	protected Dialog reminder;
	protected Map<Interactable, Dialog> onClick = new HashMap<>();
	protected Map<InteractableNPC, Consumer<NPCClickEvent>> onNPCInteract = new HashMap<>();
	protected Map<InteractableEntity, Consumer<PlayerInteractEntityEvent>> onEntityInteract = new HashMap<>();

	abstract public DialogInstance interact(Quester quester, QuestTaskStepProgress stepProgress);

	abstract public boolean shouldAdvance(Quester quester, QuestTaskStepProgress stepProgress);

	public void afterComplete(Quester quester) {}

}
