package gg.projecteden.nexus.features.events;

import gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22QuestTask;
import gg.projecteden.nexus.features.quests.QuestItem;
import gg.projecteden.nexus.features.quests.QuestReward;
import gg.projecteden.nexus.features.quests.interactable.Interactable;
import gg.projecteden.nexus.features.quests.interactable.InteractableEntity;
import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import gg.projecteden.nexus.features.warps.commands._WarpSubCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.quests.Quest;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.models.quests.QuesterService;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.CitizensUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public abstract class IEventCommand extends _WarpSubCommand implements Listener {
	@SuppressWarnings("FieldCanBeLocal")
	private final QuesterService questerService = new QuesterService();
	private Quester quester;

	public IEventCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			quester = questerService.get(player());
	}

	abstract public EdenEvent getEdenEvent();

	@Permission(Group.ADMIN)
	@Path("quest debug <task>")
	void quest_debug(IQuestTask task) {
		send(String.valueOf(task.get()));
	}

	@Permission(Group.ADMIN)
	@Path("quest start")
	void quest_start() {
		Quest.builder()
			.tasks(Easter22QuestTask.MAIN)
			.assign(player())
			.start();

		send(PREFIX + "Quest activated");
	}

	@Permission(Group.ADMIN)
	@Path("quest interactable tp <interactable>")
	void quest_interactable_tp(Interactable interactable) {
		Location location = null;
		if (interactable instanceof InteractableNPC npc) {
			location = CitizensUtils.locationOf(npc.getNpcId());
			if (location == null)
				error("Could not determine location of NPC &e" + interactable.getName());
		} else if (interactable instanceof InteractableEntity interactableEntity) {
			if (interactableEntity.getUuid() == null)
				error("Could not determine location of entity &e" + interactable.getName() + "&c, no UUID provided");

			final Entity entity = world().getEntity(interactableEntity.getUuid());
			if (entity == null || !entity.isValid())
				error("Could not determine location of entity &e" + interactable.getName() + "&c (chunk unloaded?)");
			location = entity.getLocation();
		} else
			error("Unsupported interactable type &e" + interactable.getClass().getSimpleName());

		player().teleportAsync(location, TeleportCause.COMMAND);
	}

	@Permission(Group.ADMIN)
	@Path("quest item <item> [amount]")
	void quest_item(QuestItem item, @Arg("1") int amount) {
		giveItems(item.get(), amount);
	}

	@Permission(Group.ADMIN)
	@Path("quest reward <item> [amount]")
	void quest_item(QuestReward reward, @Arg("1") int amount) {
		reward.apply(quester, amount);
	}

	@TabCompleterFor(IQuestTask.class)
	List<String> tabCompleteIQuestTask(String filter) {
		return tabCompleteEnum(filter, getEdenEvent().getConfig().tasks());
	}

	@ConverterFor(IQuestTask.class)
	IQuestTask convertToIQuestTask(String value) {
		return (IQuestTask) convertToEnum(value, getEdenEvent().getConfig().tasks());
	}

	@TabCompleterFor(Interactable.class)
	List<String> tabCompleteInteractable(String filter) {
		return new ArrayList<>() {{
			addAll(tabCompleteEnum(filter, getEdenEvent().getConfig().npcs()));
			addAll(tabCompleteEnum(filter, getEdenEvent().getConfig().entities()));
		}};
	}

	@ConverterFor(Interactable.class)
	Interactable convertToInteractable(String value) {
		try {
			return (Interactable) convertToEnum(value, getEdenEvent().getConfig().npcs());
		} catch (InvalidInputException ignore1) {
			try {
				return (Interactable) convertToEnum(value, getEdenEvent().getConfig().entities());
			} catch (InvalidInputException ignore2) {}
		}

		throw new InvalidInputException(Interactable.class.getSimpleName() + " from &e" + value + " &cnot found");
	}

	@TabCompleterFor(QuestItem.class)
	List<String> tabCompleteQuestItem(String filter) {
		return tabCompleteEnum(filter, getEdenEvent().getConfig().items());
	}

	@ConverterFor(QuestItem.class)
	QuestItem convertToQuestItem(String value) {
		return (QuestItem) convertToEnum(value, getEdenEvent().getConfig().items());
	}

	@TabCompleterFor(QuestReward.class)
	List<String> tabCompleteQuestReward(String filter) {
		return tabCompleteEnum(filter, getEdenEvent().getConfig().rewards());
	}

	@ConverterFor(QuestReward.class)
	QuestReward convertToQuestReward(String value) {
		return (QuestReward) convertToEnum(value, getEdenEvent().getConfig().rewards());
	}

	@Override
	public WarpType getWarpType() {
		return getEdenEvent().getConfig().warpType();
	}

}
