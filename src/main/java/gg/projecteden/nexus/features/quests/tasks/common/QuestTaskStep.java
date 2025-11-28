package gg.projecteden.nexus.features.quests.tasks.common;

import gg.projecteden.nexus.features.events.waypoints.IWaypoint;
import gg.projecteden.nexus.features.listeners.events.LivingEntityKilledByPlayerEvent;
import gg.projecteden.nexus.features.quests.interactable.Interactable;
import gg.projecteden.nexus.features.quests.interactable.InteractableEntity;
import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import gg.projecteden.nexus.features.quests.interactable.instructions.Dialog;
import gg.projecteden.nexus.features.quests.interactable.instructions.DialogInstance;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteringRegionEvent;
import gg.projecteden.nexus.models.quests.QuestTaskStepProgress;
import gg.projecteden.nexus.models.quests.Quester;
import kotlin.Pair;
import lombok.Data;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Data
public abstract class QuestTaskStep<
	TaskType extends QuestTask<TaskType, TaskStepType>,
	TaskStepType extends QuestTaskStep<TaskType, TaskStepType>
> {
	public Interactable interactable;
	protected ComponentLike objective;
	protected Dialog dialog;
	public Dialog reminder;
	public IWaypoint waypoint;
	protected Map<Interactable, Dialog> onClick = new HashMap<>();
	protected Map<InteractableNPC, Consumer<NPCClickEvent>> onNPCInteract = new HashMap<>();
	protected Map<InteractableEntity, Consumer<PlayerInteractEntityEvent>> onEntityInteract = new HashMap<>();
	protected Map<Class<? extends LivingEntity>, BiConsumer<LivingEntityKilledByPlayerEvent, LivingEntity>> onLivingEntityKilledByPlayer = new HashMap<>();
	protected Map<Pair<List<Material>, List<Action>>, BiConsumer<PlayerInteractEvent, Block>> onBlockInteract = new HashMap<>();
	protected Map<List<Material>, BiConsumer<BlockDropItemEvent, Block>> onBlockDropItem = new HashMap<>();
	protected Map<String, Map<String, Consumer<PlayerEnteringRegionEvent>>> onRegionEntering = new HashMap<>();

	abstract public DialogInstance interact(Quester quester, QuestTaskStepProgress stepProgress);

	abstract public boolean shouldAdvance(Quester quester, QuestTaskStepProgress stepProgress);

	public void afterComplete(Quester quester) {}

}
