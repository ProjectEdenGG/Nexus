package gg.projecteden.nexus.features.events.y2021.bearfair21.commands;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.features.events.models.Quest;
import gg.projecteden.nexus.features.events.models.QuestStage;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.BearFair21Interactables;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.BearFair21Seeker;
import gg.projecteden.nexus.features.events.y2021.bearfair21.islands.BearFair21MinigameNightIsland;
import gg.projecteden.nexus.features.events.y2021.bearfair21.islands.BearFair21MinigameNightIsland.RouterMenu;
import gg.projecteden.nexus.features.events.y2021.bearfair21.islands.BearFair21MinigameNightIsland.ScrambledCablesMenu;
import gg.projecteden.nexus.features.events.y2021.bearfair21.quests.BearFair21TreasureChests;
import gg.projecteden.nexus.features.events.y2021.bearfair21.quests.clientside.BearFair21ClientsideContentManager;
import gg.projecteden.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC;
import gg.projecteden.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21Collector;
import gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources.fishing.BearFair21FishingLoot.JunkWeight;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.*;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.bearfair21.*;
import gg.projecteden.nexus.models.bearfair21.BearFair21Config.BearFair21ConfigOption;
import gg.projecteden.nexus.models.bearfair21.ClientsideContent.Content;
import gg.projecteden.nexus.models.bearfair21.ClientsideContent.Content.ContentCategory;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.StringUtils.ProgressBar;
import gg.projecteden.nexus.utils.StringUtils.ProgressBar.SummaryStyle;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Disabled
@HideFromWiki
@Aliases({"bf21", "bearfair"})
public class BearFair21Command extends CustomCommand {
	private final ClientsideContentService contentService = new ClientsideContentService();
	private final ClientsideContent clientsideContent = contentService.get0();

	private final BearFair21UserService userService = new BearFair21UserService();

	private final BearFair21ConfigService configService = new BearFair21ConfigService();
	private final BearFair21Config config = configService.get0();

	private final List<Content> contentList = clientsideContent.getContentList();

	public BearFair21Command(CommandEvent event) {
		super(event);
	}

	@Path
	void warp() {
		runCommand("bearfair21warp");
	}

	@Permission(Group.ADMIN)
	@Path("resetPugmas <player>")
	void resetPugmasQuest(BearFair21User user) {
		user.setQuestStage_Pugmas(QuestStage.NOT_STARTED);
		user.setPugmasCompleted(false);
		user.setPresentNdx(0);
		userService.save(user);
		send("Reset Pugmas quest variables for: " + user.getNickname());
	}

	@Permission(Group.ADMIN)
	@Path("strengthTest")
	void strengthTest() {
		commandBlock();
		BearFair21Interactables.strengthTest();
	}

	@Permission(Group.ADMIN)
	@Path("seeker")
	void seeker() {
		send("Find the crimson button");
		BearFair21Seeker.addPlayer(player());
	}

	@Path("toCollector")
	@Permission(Group.ADMIN)
	public void toCollector() {
		player().teleportAsync(BearFair21Collector.getCurrentLoc());
	}

	@Path("progress [player]")
	@Description("View your event progress")
	void progress(@Arg(value = "self", permission = Group.STAFF) BearFair21User user) {
		final LocalDate start = LocalDate.of(2021, 6, 28);
		final LocalDate now = LocalDate.now();
		int day = start.until(now).getDays() + 1;

		send(PREFIX + "Event progress (Day &e#" + day + "&7/7&3):");
		line();

		send("&6&lQuests");
		for (BearFair21UserQuestStageHelper quest : BearFair21UserQuestStageHelper.values()) {
			JsonBuilder json = json();
			final QuestStage stage = quest.getter().apply(user);
			String instructions = BearFair21Quest.valueOf(quest.name()).getInstructions(user, stage);

			if (stage == QuestStage.COMPLETE)
				json.next("&f  &a☑ &3" + camelCase(quest) + " &7- &aComplete");
			else if (stage == QuestStage.NOT_STARTED || stage == QuestStage.INELIGIBLE)
				json.next("&f  &7☐ &3" + camelCase(quest) + " &7- &cNot started" + (instructions == null ? "" : " &7- " + instructions));
			else
				json.next("&f  &7☐ &3" + camelCase(quest) + " &7- &eIn progress" + (instructions == null ? "" : " &7- " + instructions));

			send(json);
		}

		/*
		line();
		send("&6&lFairgrounds");
		for (BF21PointSource source : BF21PointSource.values()) {
			JsonBuilder json = json();
			final int dailyTokensLeft = Math.abs(BearFair21.getDailyTokensLeft(user.getOfflinePlayer(), source, 0));

			if (dailyTokensLeft == 0)
				json.next("&f  &a☑ &3" + camelCase(source) + " &7- &aComplete");
			else
				json.next("&f  &7☐ &3" + camelCase(source) + " &7- &cIncomplete &3(&e" + dailyTokensLeft + " &3tokens left)");

			send(json);
		}
		 */

		line();
		send("&6&lTreasure Chests");
		final int found = user.getTreasureChests().size();
		final int total = BearFair21TreasureChests.getLocations().size();
		send("&f  " + (found == total ? "&a☑" : "&7☐") + " &3Found: " + ProgressBar.builder().progress(found).goal(total).summaryStyle(SummaryStyle.COUNT).length(40).build());

		line();
		if (day < 7) {
			send("&3Next day begins in &e" + Timespan.of(now.plusDays(1)).format());
			line();
		}
	}

	// Config

	@Permission(Group.ADMIN)
	@Path("config <option> <boolean>")
	void config(BearFair21ConfigOption option, boolean enabled) {
		config.setEnabled(option, enabled);
		configService.save(config);
		send(PREFIX + (enabled ? "&aEnabled" : "&cDisabled") + " &3config option &e" + camelCase(option));
	}

	// Command Blocks

	@Path("moveCollector")
	@Permission(Group.ADMIN)
	public void moveCollector() {
		commandBlock();
		BearFair21Collector.move();
	}

	@Path("yachtHorn")
	@Permission(Group.ADMIN)
	public void yachtHorn() {
		commandBlock();
		BlockCommandSender sender = (BlockCommandSender) event.getSender();
		Location loc = sender.getBlock().getLocation();
		World world = loc.getWorld();
		if (world == null)
			return;

		new SoundBuilder(Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED).location(loc).volume(4).pitch(0.1).play();
		Tasks.wait(TickTime.SECOND.x(2), () ->
			new SoundBuilder(Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED).location(loc).volume(4).pitch(0.1).play());
	}

	@Path("metNPCs")
	@Permission(Group.ADMIN)
	public void metNPCs() {
		Set<Integer> npcs = userService.get(player()).getMetNPCs();
		if (Nullables.isNullOrEmpty(npcs))
			error("User has not met any npcs");

		send("Has met: ");
		for (Integer npcId : npcs) {
			BearFair21NPC npc = BearFair21NPC.from(npcId);
			if (npc != null)
				send(" - " + npc.getNpcNameAndJob() + " (" + npcId + ")");
		}
	}

	@Path("nextStepNPCs")
	@Permission(Group.ADMIN)
	public void nextStepNPCs() {
		Set<Integer> npcs = userService.get(player()).getNextStepNPCs();
		if (Nullables.isNullOrEmpty(npcs))
			error("User has not have any nextStepNPCs");

		send("Next Step NPCs: ");
		for (Integer npcId : npcs) {
			BearFair21NPC npc = BearFair21NPC.from(npcId);
			if (npc != null)
				send(" - " + npc.getNpcNameAndJob() + " (" + npcId + ")");
		}
	}

	@Getter
	@AllArgsConstructor
	@Accessors(fluent = true)
	public enum BearFair21UserQuestStageHelper {
		MAIN(BearFair21User::getQuestStage_Main, BearFair21User::setQuestStage_Main),
		RECYCLER(BearFair21User::getQuestStage_Recycle, BearFair21User::setQuestStage_Recycle),
		BEEKEEPER(BearFair21User::getQuestStage_BeeKeeper, BearFair21User::setQuestStage_BeeKeeper),
		LUMBERJACK(BearFair21User::getQuestStage_Lumberjack, BearFair21User::setQuestStage_Lumberjack),
		MINIGAME_NIGHT(BearFair21User::getQuestStage_MGN, BearFair21User::setQuestStage_MGN),
		PUGMAS(BearFair21User::getQuestStage_Pugmas, BearFair21User::setQuestStage_Pugmas),
		HALLOWEEN(BearFair21User::getQuestStage_Halloween, BearFair21User::setQuestStage_Halloween),
		SUMMER_DOWN_UNDER(BearFair21User::getQuestStage_SDU, BearFair21User::setQuestStage_SDU),
		;

		private final Function<BearFair21User, QuestStage> getter;
		private final BiConsumer<BearFair21User, QuestStage> setter;
	}

	@Getter
	@AllArgsConstructor
	public enum BearFair21Quest implements Quest<BearFair21User> {
		MAIN(user -> new HashMap<>() {{
			put(QuestStage.NOT_STARTED, "Find " + BearFair21NPC.MAYOR.getNpcNameAndJob() + " in Honeywood village");
			for (QuestStage stage : List.of(QuestStage.STARTED, QuestStage.STEP_ONE, QuestStage.STEP_TWO, QuestStage.STEP_THREE, QuestStage.STEP_FOUR, QuestStage.STEP_FIVE, QuestStage.STEP_SIX))
				put(stage, "Talk to " + BearFair21NPC.MAYOR.getNpcNameAndJob());
		}}),
		RECYCLER(user -> new HashMap<>() {{
			put(QuestStage.NOT_STARTED, "Find " + BearFair21NPC.FISHERMAN2.getNpcNameAndJob() + " by the lake");
			put(QuestStage.STARTED, "Recycled trash: " + ProgressBar.builder().progress(user.getRecycledItems()).goal(JunkWeight.MIN.getAmount()).summaryStyle(SummaryStyle.PERCENT).length(160).build());
		}}),
		BEEKEEPER(user -> new HashMap<>() {{
			put(QuestStage.NOT_STARTED, "Find " + BearFair21NPC.BEEKEEPER.getNpcNameAndJob() + " by the bee colony");
			put(QuestStage.STARTED, "Find the beehive");
			put(QuestStage.STEP_ONE, "Talk to the Queen Bee");
			put(QuestStage.STEPS_DONE, "Talk to " + BearFair21NPC.BEEKEEPER.getNpcNameAndJob());
		}}),
		LUMBERJACK(user -> new HashMap<>() {{
			put(QuestStage.NOT_STARTED, "Find " + BearFair21NPC.LUMBERJACK.getNpcNameAndJob() + " in the saw mill");
			put(QuestStage.STARTED, "Talk to " + BearFair21NPC.LUMBERJACK.getNpcNameAndJob());
		}}),
		MINIGAME_NIGHT(user -> new HashMap<>() {{
			put(QuestStage.NOT_STARTED, "Find " + BearFair21NPC.AXEL.getNpcNameAndJob() + " by the Game Gallery");
			put(QuestStage.STARTED, "Talk to " + BearFair21NPC.MGN_CUSTOMER_1);
			put(QuestStage.STEP_ONE, "Talk to " + BearFair21NPC.MGN_CUSTOMER_1);
			put(QuestStage.STEP_TWO, "Answer the phone");
			put(QuestStage.STEP_THREE, "Repair the laptop");
			put(QuestStage.STEP_FOUR, "Call " + BearFair21NPC.MGN_CUSTOMER_2.getNpcNameAndJob() + " back");
			put(QuestStage.STEP_FIVE, "Answer the phone");
			put(QuestStage.STEP_SIX, "Answer the phone");
			put(QuestStage.STEP_SEVEN, "Talk to " + BearFair21NPC.ADMIRAL.getNpcNameAndJob());
			put(QuestStage.STEP_EIGHT, "Answer the phone");
		}}),
		PUGMAS(user -> new HashMap<>() {{
			put(QuestStage.NOT_STARTED, "Find " + BearFair21NPC.PUGMAS_MAYOR.getNpcNameAndJob() + " by the Pugmas Tree");
			for (QuestStage stage : List.of(QuestStage.STARTED, QuestStage.STEP_ONE))
				put(stage, "Talk to the " + BearFair21NPC.GRINCH.getNpcNameAndJob());
			put(QuestStage.STEP_TWO, "Find the presents");
		}}),
		HALLOWEEN(user -> new HashMap<>() {{
			put(QuestStage.NOT_STARTED, "Find " + BearFair21NPC.JOSE.getNpcNameAndJob() + " outside the Coco village");
			put(QuestStage.STARTED, "Talk to " + BearFair21NPC.SANTIAGO.getNpcNameAndJob());
			put(QuestStage.STEP_ONE, "Find " + BearFair21NPC.ANA.getNpcNameAndJob());
			put(QuestStage.STEP_TWO, "Talk to " + BearFair21NPC.ANA.getNpcNameAndJob());
			put(QuestStage.STEP_THREE, "Talk to " + BearFair21NPC.ANA.getNpcNameAndJob());
			put(QuestStage.STEPS_DONE, "Talk to " + BearFair21NPC.JOSE.getNpcNameAndJob());
		}}),
		SUMMER_DOWN_UNDER(user -> new HashMap<>() {{
			put(QuestStage.NOT_STARTED, "Find " + BearFair21NPC.BRUCE.getNpcNameAndJob() + " by the ute");
			put(QuestStage.STARTED, "Talk to " + BearFair21NPC.KYLIE.getNpcNameAndJob());
			put(QuestStage.STEP_ONE, "Get wheat for " + BearFair21NPC.KYLIE.getNpcNameAndJob());
			put(QuestStage.STEP_TWO, "Milk Daisy the cow");
			put(QuestStage.STEP_THREE, "Talk to " + BearFair21NPC.MEL_GIBSON.getNpcNameAndJob());
			put(QuestStage.STEP_FOUR, "Talk to " + BearFair21NPC.MILO.getNpcNameAndJob());
			put(QuestStage.STEP_FIVE, "Collect 7 feathers and bring them to " + BearFair21NPC.BRUCE.getNpcNameAndJob());
			put(QuestStage.STEP_SIX, "Head down to the cave");
			put(QuestStage.STEP_SEVEN, "Head deeper into the cave");
			put(QuestStage.STEPS_DONE, "Talk to the townsfolk");
			put(QuestStage.FOUND_ALL, "Talk to " + BearFair21NPC.BRUCE.getNpcNameAndJob());
		}});

		private final Function<BearFair21User, Map<QuestStage, String>> instructions;
	}

	@Permission(Group.ADMIN)
	@Path("setQuestStage <quest> <stage> [player]")
	void setQuestStage(BearFair21UserQuestStageHelper quest, QuestStage stage, @Arg("self") BearFair21User player) {
		userService.edit(player, user -> quest.setter.accept(user, stage));
		send(PREFIX + (isSelf(player) ? "Your" : player.getNickname() + "'s") + " " + camelCase(quest) + " quest stage to set to " + camelCase(stage));
	}

	@Permission(Group.ADMIN)
	@Path("mgn scrambledCables")
	void scrambledCables() {
		new ScrambledCablesMenu().open(player());
	}

	@Permission(Group.ADMIN)
	@Path("mgn router")
	void router() {
		new RouterMenu().open(player());
	}

	@Permission(Group.ADMIN)
	@Path("mgn solder reset")
	void solderReset() {
		BearFair21MinigameNightIsland.setActiveSolder(false);
		send("Solder reset");
	}

	@Confirm
	@Permission(Group.ADMIN)
	@Path("clientside category remove [category]")
	void clientsideClear(ContentCategory category) {
		BearFair21User user = userService.get(uuid());
		if (category == null) {
			for (ContentCategory _category : ContentCategory.values()) {
				BearFair21ClientsideContentManager.removeCategory(user, _category);
			}

			userService.save(user);

			send("removed all locations from " + user.getNickname());
			return;
		}

		BearFair21ClientsideContentManager.removeCategory(user, category);
		userService.save(user);
		send("removed " + category + " Content Category");
	}

	@Permission(Group.ADMIN)
	@Path("clientside category add <category> [player]")
	void clientsideAddAll(ContentCategory category, @Arg("self") Player player) {
		BearFair21User user = userService.get(player);
		Set<ContentCategory> categories = user.getContentCategories();

		categories.add(category);
		user.setContentCategories(categories);
		userService.save(user);

		send(player.getName() + " visible categories: " + Arrays.toString(user.getContentCategories().toArray()));

		BearFair21ClientsideContentManager.sendSpawnContent(player, contentService.getList(category));
	}

	@Confirm
	@Permission(Group.ADMIN)
	@Path("clientside clear <category>")
	void clientsideClearCategory(ContentCategory category) {
		clientsideContent.getContentList().removeIf(content -> content.getCategory() == category);
		contentService.save(clientsideContent);
		send("Cleared category");
	}

	@Permission(Group.ADMIN)
	@Path("clientside new <category>")
	void clientsideNew(ContentCategory category) {
		Entity entity = getTargetEntity();
		if (entity == null) {
			Block block = getTargetBlock();
			if (gg.projecteden.nexus.utils.Nullables.isNullOrAir(block))
				error("Entity is null && Block is null or air");

			setupBlockContent(block, category);
			send("Added block: " + block.getType());
		} else if (entity instanceof ItemFrame) {
			setupItemFrameContent((ItemFrame) entity, category);
			send("Added item frame");
		} else {
			error("That's not a supported entity type: " + entity.getType().name());
		}
	}

	@Permission(Group.ADMIN)
	@Path("clientside new schematic <category> <schematic>")
	void clientsideNew(ContentCategory category, String schematic) {
		setupSchematicContent(location(), schematic, category);
		send("Added schematic " + schematic);
	}

	@Permission(Group.ADMIN)
	@Path("clientside new current <category>")
	void clientsideNewCurrent(ContentCategory category) {
		setupBlockContent(block(), category);
		send("Added block: " + block().getType());
	}

	@Permission(Group.ADMIN)
	@Path("clientside list")
	void clientsideList() {
		List<Content> food = new ArrayList<>();
		List<Content> balloons = new ArrayList<>();
		List<Content> festoon = new ArrayList<>();
		List<Content> banners = new ArrayList<>();
		List<Content> presents = new ArrayList<>();
		List<Content> sawmill = new ArrayList<>();
		List<Content> cable = new ArrayList<>();
		List<Content> unlisted = new ArrayList<>();

		for (Content content : contentList) {
			switch (content.getCategory()) {
				case FOOD -> food.add(content);
				case BALLOON -> balloons.add(content);
				case FESTOON -> festoon.add(content);
				case BANNER -> banners.add(content);
				case PRESENT -> presents.add(content);
				case SAWMILL -> sawmill.add(content);
				case CABLE -> cable.add(content);
				default -> unlisted.add(content);
			}
		}

		send("Food: " + food.size());
		send("Balloons: " + balloons.size());
		send("Festoon: " + festoon.size());
		send("Banner: " + banners.size());
		send("Present: " + presents.size());
		send("SawMill: " + sawmill.size());
		send("Cable: " + cable.size());
		send("Unlisted: " + unlisted.size());

		List<Content> contentList = new ArrayList<>();
		contentList.addAll(food);
		contentList.addAll(balloons);
		contentList.addAll(festoon);
		contentList.addAll(banners);
		contentList.addAll(presents);
		contentList.addAll(sawmill);
		contentList.addAll(cable);
		contentList.addAll(unlisted);

		StringBuilder string = new StringBuilder();
		for (Content content : contentList) {
			string.append("\n")
					.append(StringUtils.camelCase(content.getCategory().name()))
					.append(": ")
					.append(StringUtils.camelCase(content.getMaterial().name()))
					.append(" - ")
					.append(StringUtils.getShortLocationString(content.getLocation()));
		}

		String url = StringUtils.paste(string.toString());
		send(json("&e&l[Click to Open]").url(url).hover(url));
	}

//	@Permission(Group.ADMIN)
//	@Path("clientside remove")
//	void clientsideRemove() {
//		int count = 0;
//		for (Content content : contentList) {
//			if (content.getLocation().equals(location().toBlockLocation())) {
//				contentList.remove(content);
//				count++;
//			}
//		}
//
//		if (count == 0)
//			error("There is no content at " + StringUtils.getShortLocationString(location().toBlockLocation()));
//
//		clientsideContent.setContentList(contentList);
//		contentService.save(clientsideContent);
//	}

	private void setupBlockContent(Block block, ContentCategory category) {
		ClientsideContent.Content content = new Content();
		content.setLocation(block.getLocation().toBlockLocation());
		content.setCategory(category);
		content.setMaterial(block.getType());
		if (block.getBlockData() instanceof Directional)
			content.setBlockFace(((Directional) block.getBlockData()).getFacing());
		addContent(content);
	}

	private void setupSchematicContent(Location location, String schematic, ContentCategory category) {
		ClientsideContent.Content content = new Content();
		content.setLocation(location.toBlockLocation());
		content.setCategory(category);
		content.setSchematic(schematic);
		addContent(content);
	}

	private void setupItemFrameContent(ItemFrame itemFrame, ContentCategory category) {
		ClientsideContent.Content content = new Content();
		content.setLocation(itemFrame.getLocation().toBlockLocation());
		content.setCategory(category);
		content.setMaterial(Material.ITEM_FRAME);
		content.setItemStack(itemFrame.getItem());
		content.setBlockFace(itemFrame.getFacing());
		content.setRotation(itemFrame.getRotation());
		addContent(content);

	}

	private void addContent(ClientsideContent.Content content) {
		for (Content _content : contentList) {
			if (_content.getLocation().equals(content.getLocation()))
				error("Duplicate content location");
		}

		contentList.add(content);
		clientsideContent.setContentList(contentList);
		contentService.save(clientsideContent);
	}
}
