package gg.projecteden.nexus.features.events;

import gg.projecteden.nexus.features.events.models.EventFishingLoot.EventFishingLootCategory;
import gg.projecteden.nexus.features.events.models.EventFishingLoot.FishingLoot;
import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.quests.CommonQuestItem;
import gg.projecteden.nexus.features.quests.QuestItem;
import gg.projecteden.nexus.features.quests.QuestReward;
import gg.projecteden.nexus.features.quests.interactable.Interactable;
import gg.projecteden.nexus.features.quests.interactable.InteractableEntity;
import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import gg.projecteden.nexus.features.quests.tasks.common.IQuest;
import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import gg.projecteden.nexus.features.warps.commands._WarpSubCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
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
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.StringUtils.ProgressBar;
import gg.projecteden.nexus.utils.StringUtils.ProgressBar.ProgressBarBuilder;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@NoArgsConstructor
public abstract class IEventCommand extends _WarpSubCommand implements Listener {
	@SuppressWarnings("FieldCanBeLocal")
	protected final QuesterService questerService = new QuesterService();
	protected Quester quester;

	public IEventCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent()) {
			if (!getEdenEvent().isEventActive(player()))
				error("This event is not currently active");

			quester = questerService.get(player());
		}
	}

	abstract public EdenEvent getEdenEvent();

	@Path("quest progress [player]")
	protected void quest_progress(@Arg(value = "self", permission = Group.STAFF) Quester quester) {
		send(PREFIX + "Quest Progress");
		for (IQuest iQuest : getEdenEvent().getQuests()) {
			final boolean started = quester.hasStarted(iQuest);

			if (!started) {
				send("&3 " + iQuest.getName() + " &7- &cNot started");
				continue;
			}

			final Quest quest = quester.getQuest(iQuest);

			if (!quest.isComplete()) {
				var objective = quest.getCurrentTaskStep().getObjective();
				send("&3 " + iQuest.getName() + " &7- &eStarted (" + quest.getCompletedSteps() + "/" + quest.getTotalSteps() + " steps)");
				send("&7   - " + objective);
				continue;
			}

			send("&3 " + iQuest.getName() + " &7- &aCompleted");
		}
	}

	@Permission(Group.ADMIN)
	@Path("stats quest progress [page]")
	@Description("Print all users' quest progress")
	protected void stats_quest_progress(@Arg("1") int page) {
		final List<IQuest> quests = getEdenEvent().getQuests();

		Map<Quester, Integer> counts = new HashMap<>() {{
			for (Quester quester : new QuesterService().getAll()) {
				var started = 0;
				var completed = 0;
				for (IQuest quest : quests) {
					if (quester.hasStarted(quest))
						++started;
					if (quester.hasCompleted(quest))
						++completed;
				}

				if (started > 0)
					put(quester, completed);
			}
		}};

		send(PREFIX + "Quest progress");

		if (page == 1) {
			final int completed = counts.values().stream().mapToInt(i -> i).sum();
			send(" &7Questers: &e" + counts.keySet().size());
			send(" &7Quests complete: &e" + completed);
			send(" &7Quests incomplete: &e" + (quests.size() * counts.keySet().size() - completed));
			line();
		}

		final BiFunction<Quester, String, JsonBuilder> formatter = (user, index) -> {
			final ProgressBarBuilder bar = ProgressBar.builder()
				.goal(quests.size())
				.progress(counts.get(user))
				.seamless(true)
				.length(150);

			return json(bar.build()).next(" &e" + counts.get(user) + "/" + quests.size() + " &7- &e" + user.getNickname());
		};

		new Paginator<Quester>()
			.values(Utils.sortByValueReverse(counts).keySet())
			.formatter(formatter)
			.command("/" + getEdenEvent().getName().toLowerCase() + " stats quest progress")
			.page(page)
			.send();
	}

	@Permission(Group.ADMIN)
	@Path("quest debug <quest>")
	@Description("Print a quest to chat")
	void quest_debug(IQuest quest) {
		send("Quest:" + quest);
		send("Tasks:");
		for (IQuestTask task : quest.getTasks())
			send(String.valueOf(task.builder()));
	}

	@Permission(Group.ADMIN)
	@Path("quest start <quest>")
	@Description("Start an event's quest")
	void quest_start(IQuest quest) {
		quest.assign(player());
		send(PREFIX + "Quest activated");
	}

	@Permission(Group.ADMIN)
	@Path("quest reset <quest>")
	@Description("Reset an event's quest progress")
	void quest_reset(IQuest quest) {
		Quester.of(player()).getQuests().removeIf(_quest -> _quest.getQuest() == quest);
		send(PREFIX + "Quest reset");
	}

	@Permission(Group.ADMIN)
	@Path("quest interactable tp <interactable>")
	@Description("Teleport to an interactable")
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
	@Description("Spawn a quest item")
	void quest_item(QuestItem item, @Arg("1") int amount) {
		giveItems(item.get(), amount);
	}

	@Permission(Group.ADMIN)
	@Path("quest item menu")
	@Description("View all quest items")
	void quest_item_menu() {
		new QuestItemsProvider(player(), getEdenEvent());
	}

	@Getter
	public static class QuestItemsProvider implements TemporaryMenuListener {
		private final Player player;
		private final EdenEvent edenEvent;

		@Override
		public String getTitle() {
			return edenEvent.getName() + " Quest Items";
		}

		public QuestItemsProvider(Player viewer, EdenEvent edenEvent) {
			this.player = viewer;
			this.edenEvent = edenEvent;
			Enum<? extends QuestItem>[] questItems = edenEvent.getConfig().items().getEnumConstants();
			var items = new ArrayList<>(Arrays.stream(questItems).map(questItem -> ((QuestItem) questItem).get()).toList());
			items.addAll(Arrays.stream(CommonQuestItem.values()).map(CommonQuestItem::get).toList());

			open(items);
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
		}
	}

	@Permission(Group.ADMIN)
	@Path("quest reward <item> [amount]")
	@Description("Receive a quest reward")
	void quest_item(QuestReward reward, @Arg("1") int amount) {
		reward.apply(quester, amount);
	}

	@Permission(Group.ADMIN)
	@Path("fishing stats category")
	void statsCategory() {
		for (EventFishingLootCategory category : EventFishingLootCategory.values())
			send(category.name() + " " + category.getWeight() + " | " + StringUtils.getDf().format(category.getChance()) + "%");
	}

	@Permission(Group.ADMIN)
	@Path("fishing stats loot")
	void statsLoot() {
		for (FishingLoot loot : getEdenEvent().getFishingLoot())
			send(loot.getId() + " " + loot.getWeight() + " | " + StringUtils.getDf().format(getChance(loot)) + "%");
	}

	public double getChance(FishingLoot loot) {
		double sum = 0;
		EventFishingLootCategory category = loot.getCategory();
		for (FishingLoot _loot : getEdenEvent().getFishingLoot())
			if (category.equals(_loot.getCategory()))
				sum += loot.getWeight();

		return (loot.getWeight() / sum) * 100;
	}

	@Getter
	@AllArgsConstructor
	private enum LureLevel {
		NONE(5, 30),
		ONE(5, 25),
		TWO(5, 20),
		THREE(5, 15);

		private final int min;
		private final int max;

		public int getWaitTime() {
			return RandomUtils.randomInt(min, max);
		}
	}

	private int getCategorySize(List<FishingLoot> loot, EventFishingLootCategory category) {
		int count = 0;
		for (FishingLoot fishingLoot : loot) {
			if (fishingLoot.getCategory().equals(category))
				count++;
		}
		return count;
	}

	@Permission(Group.ADMIN)
	@Path("fishing simulate <lure> <minutes>")
	void simulateLoot(LureLevel lure, int minutes) {
		int maxSeconds = minutes * 60;
		List<FishingLoot> fullCatch = new ArrayList<>();
		for (int seconds = 0; seconds < maxSeconds; seconds++) {
			seconds += lure.getWaitTime();
			fullCatch.add(getEdenEvent().getFishingListener().getFishingLoot(player()));
		}

		send("FISH: " + getCategorySize(fullCatch, EventFishingLootCategory.FISH));
		send("JUNK: " + getCategorySize(fullCatch, EventFishingLootCategory.JUNK));
		send("TREASURE: " + getCategorySize(fullCatch, EventFishingLootCategory.TREASURE));
		send("==============");
		send("Total: " + fullCatch.size());
	}

	@TabCompleterFor(IQuestTask.class)
	List<String> tabCompleteIQuestTask(String filter) {
		return tabCompleteEnum(filter, getEdenEvent().getConfig().tasks());
	}

	@ConverterFor(IQuestTask.class)
	IQuestTask convertToIQuestTask(String value) {
		return (IQuestTask) convertToEnum(value, getEdenEvent().getConfig().tasks());
	}

	@TabCompleterFor(IQuest.class)
	List<String> tabCompleteIQuest(String filter) {
		return tabCompleteEnum(filter, getEdenEvent().getConfig().quests());
	}

	@ConverterFor(IQuest.class)
	IQuest convertToIQuest(String value) {
		return (IQuest) convertToEnum(value, getEdenEvent().getConfig().quests());
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
