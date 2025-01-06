package gg.projecteden.nexus.features.events.y2020.pugmas20.commands;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.features.events.models.QuestStage;
import gg.projecteden.nexus.features.events.y2020.pugmas20.AdventChests;
import gg.projecteden.nexus.features.events.y2020.pugmas20.Pugmas20;
import gg.projecteden.nexus.features.events.y2020.pugmas20.Pugmas20Train;
import gg.projecteden.nexus.features.events.y2020.pugmas20.menu.providers.AdventProvider;
import gg.projecteden.nexus.features.events.y2020.pugmas20.models.AdventChest;
import gg.projecteden.nexus.features.events.y2020.pugmas20.models.AdventChest.District;
import gg.projecteden.nexus.features.events.y2020.pugmas20.models.Merchants.MerchantNPC;
import gg.projecteden.nexus.features.events.y2020.pugmas20.models.QuestNPC;
import gg.projecteden.nexus.features.events.y2020.pugmas20.quests.LightTheTree;
import gg.projecteden.nexus.features.events.y2020.pugmas20.quests.OrnamentVendor;
import gg.projecteden.nexus.features.events.y2020.pugmas20.quests.OrnamentVendor.Ornament;
import gg.projecteden.nexus.features.events.y2020.pugmas20.quests.OrnamentVendor.PugmasTreeType;
import gg.projecteden.nexus.features.events.y2020.pugmas20.quests.Quests.Pugmas20Quest;
import gg.projecteden.nexus.features.events.y2020.pugmas20.quests.Quests.Pugmas20QuestStageHelper;
import gg.projecteden.nexus.features.events.y2020.pugmas20.quests.TheMines;
import gg.projecteden.nexus.features.events.y2020.pugmas20.quests.TheMines.OreType;
import gg.projecteden.nexus.features.events.y2020.pugmas20.quests.ToyTesting;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.*;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.eventuser.EventUser;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.pugmas20.Pugmas20User;
import gg.projecteden.nexus.models.pugmas20.Pugmas20UserService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.MerchantBuilder.TradeBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Disabled
@NoArgsConstructor
@Environments(Env.PROD)
//@Redirect(from = "/advent", to = "/pugmas advent")
//@Redirect(from = "/district", to = "/pugmas district")
//@Redirect(from = "/waypoint", to = "/pugmas waypoint")
public class Pugmas20Command extends CustomCommand implements Listener {
	private final String timeLeft = Timespan.of(Pugmas20.openingDay).format();
	private final Pugmas20UserService pugmasService = new Pugmas20UserService();
	private Pugmas20User pugmasUser;
	private final EventUserService eventUserService = new EventUserService();
	private EventUser eventUser;

	public Pugmas20Command(CommandEvent event) {
		super(event);
		PREFIX = Pugmas20.PREFIX;
		if (isPlayer()) {
			pugmasUser = pugmasService.get(player());
			eventUser = eventUserService.get(player());
		}
	}

	@Path
	void pugmas() {
		LocalDate now = LocalDate.now();
		if (Pugmas20.isBeforePugmas(now) && !isStaff())
			error("Soon™ (" + timeLeft + ")");

		if (pugmasUser.isWarped()) {
			player().teleportAsync(Pugmas20.getSubsequentSpawn(), TeleportCause.COMMAND);
		} else {
			player().teleportAsync(Pugmas20.getInitialSpawn(), TeleportCause.COMMAND);
			pugmasUser.setWarped(true);
			pugmasService.save(pugmasUser);
		}
	}

	@Path("muteTrain [muted]")
	@Description("Mute train sounds")
	void muteTrain(Boolean muted) {
		if (muted == null)
			muted = !pugmasUser.isMuteTrain();

		pugmasUser.setMuteTrain(muted);
		pugmasService.save(pugmasUser);
		send(PREFIX + "Train " + (muted ? "muted" : "unmuted"));
	}

	@Path("progress [player]")
	@Description("View your event progress")
	void progress(@Arg(value = "self", permission = Group.STAFF) Pugmas20User user) {
		LocalDate now = LocalDate.now();

		if (Pugmas20.isBeforePugmas(now))
			now = now.withYear(2020).withMonth(12).withDayOfMonth(1);

		if (Pugmas20.isPastPugmas(now))
			error("Next year!");

		if (Pugmas20.isSecondChance(now))
			now = now.withYear(2020).withMonth(12).withDayOfMonth(25);

		int day = now.getDayOfMonth();

		line(2);

		send(PREFIX + "Event progress (Day &e#" + day + "&3):");
		line();

		String advent;
		AdventChest adventChest = AdventChests.getAdventChest(day);

		if (user.getFoundDays().size() == 25)
			advent = "&a☑ &3Complete";
		else if (user.getFoundDays().contains(day))
			advent = "&a☑ &3Found today's chest";
		else if (day == 25)
			if (user.getFoundDays().size() != 24)
				advent = "&7☐ &3Find all chests before #25";
			else
				advent = "&7☐ &3Find the last chest";
		else
			advent = "&7☐ &3Find today's chest (&e#" + day + " &3in the &e" + adventChest.getDistrict().getName() + " District&3)";

		send("&6&lAdvent Chests");
		send(json("&f  " + advent + " &7- Click for info").hover("Click to open the Advent menu").command("/pugmas advent"));

		line();
		send("&6&lQuests");

		for (Pugmas20QuestStageHelper quest : Pugmas20QuestStageHelper.values()) {
			QuestStage stage = quest.getter().apply(user);
			String instructions = Pugmas20Quest.valueOf(quest.name()).getInstructions(user, stage);
			JsonBuilder json = json();
			if (quest == Pugmas20QuestStageHelper.THE_MINES && stage == QuestStage.STARTED) {
				List<String> tradesLeft = getIngotTradesLeft(user);
				if (tradesLeft.isEmpty()) {
					json.next("&f  &a☑ &3" + camelCase(quest) + " &7- &aCompleted daily quest &7- Come back tomorrow for more");
				} else {
					json.next("&f  &7☐ &3" + camelCase(quest) + " &7- &eIn progress &7- " + instructions);
					tradesLeft.add(0, "&6Today's available trades:");
					json.next(" &7&o(Hover for info)").hover(tradesLeft);
				}
			} else {
				if (stage == QuestStage.COMPLETE) {
					json.next("&f  &a☑ &3" + camelCase(quest) + " &7- &aComplete");
				} else if (stage == QuestStage.NOT_STARTED || stage == QuestStage.INELIGIBLE) {
					json.next("&f  &7☐ &3" + camelCase(quest) + " &7- &cNot started" + (instructions == null ? "" : " &7- " + instructions));
				} else {
					json.next("&f  &7☐ &3" + camelCase(quest) + " &7- &eIn progress &7- ");
					if (instructions == null)
						json.next("&c???").hover(camelCase(stage));
					else
						json.next("&7" + instructions);
				}

				if (quest == Pugmas20QuestStageHelper.TOY_TESTING && stage == QuestStage.STARTED) {
					List<String> toysLeft = QuestNPC.getUnplayedToysList(user);
					toysLeft.add(0, "&6Toys left to test:");
					json.next(" &7&o(Hover for info)").hover(toysLeft, ChatColor.WHITE);
				}

				if (quest == Pugmas20QuestStageHelper.ORNAMENT_VENDOR && stage != QuestStage.NOT_STARTED) {
					List<String> lore = getOrnamentTradesLeft(user);
					if (!lore.isEmpty()) {
						lore.add(0, "&6Available ornament trades:");
						if (stage == QuestStage.COMPLETE)
							json.next(" &7- More trades available");
						lore.add("&f");
						lore.add("&fYou get to keep any extra ornaments");
						json.next(" &7&o(Hover for info)").hover(lore, ChatColor.WHITE);
					}
				}
			}

			send(json);
		}

		line();
		if (day < 25) {
			send("&3Next day begins in &e" + Timespan.of(now.plusDays(1)).format());
			line();
		}
	}

	private List<String> getOrnamentTradesLeft(Pugmas20User user) {
		List<String> lore = new ArrayList<>();
		for (Ornament ornament : Ornament.values()) {
			if (!user.canTradeOrnament(ornament))
				continue;

			int tradesLeft = user.ornamentTradesLeft(ornament);
			lore.add("&e" + tradesLeft + " &f" + plural(camelCase(ornament) + " Ornament", tradesLeft));
		}
		return lore;
	}

	private List<String> getIngotTradesLeft(Pugmas20User user) {
		List<String> tradesLeft = new ArrayList<>();
		List<TradeBuilder> trades = MerchantNPC.THEMINES_SELLCRATE.getTrades(user);

		for (OreType oreType : OreType.values()) {
			int ingotsLeft = getIngotsLeft(user, trades, oreType);
			if (ingotsLeft > 0)
				tradesLeft.add("&e" + ingotsLeft + " &f" + camelCase(oreType));
		}
		return tradesLeft;
	}

	private int getIngotsLeft(Pugmas20User user, List<TradeBuilder> trades, OreType oreType) {
		Optional<Integer> amount = trades.stream()
				.map(tradeBuilder -> tradeBuilder.getIngredients().iterator().next())
				.filter(ingredient -> ingredient.getType() == oreType.getIngot().getType())
				.map(ItemStack::getAmount)
				.findFirst();

		int tokensLeft = Math.abs(Pugmas20.checkDailyTokens(user, "themines_" + oreType.name(), 0));
		int perToken = amount.orElse(0);

		return tokensLeft * perToken;
	}

	static {
//		AdventMenu.loadHeads();
//		new AdventChests();
	}

	@Path("advent")
	@Description("Open the Advent menu")
	void advent() {

		LocalDate now = LocalDate.now();

		if (!isAdmin()) {
			if (Pugmas20.isBeforePugmas(now))
				error("Soon™ (" + timeLeft + ")");

			if (Pugmas20.isPastPugmas(now))
				error("Next year!");

			if (Pugmas20.isSecondChance(now))
				now = now.withYear(2020).withMonth(12).withDayOfMonth(25);
		}

		new AdventProvider(now).open(player());
	}

	@Permission(Group.ADMIN)
	@Path("advent give <day>")
	void adventGive(int day) {
		AdventChests.giveAdventHead(player(), day);
	}

	@Permission(Group.ADMIN)
	@Path("advent open <day>")
	void adventOpenDay(int day) {
		AdventChests.openAdventLootInv(player(), day);
	}

	@Permission(Group.ADMIN)
	@Path("advent foundCounts")
	void adventFoundAll() {
		send(PREFIX + "Found counts:");
		new HashMap<Integer, List<String>>() {{
			pugmasService.getAll().forEach(user -> {
				List<String> names = getOrDefault(user.getLocatedDays().size(), new ArrayList<>());
				names.add(user.getName());
				put(user.getLocatedDays().size(), names);
			});
		}}.forEach(((day, names) -> send("&3" + day + " &e" + String.join(", ", names))));
	}

	@Permission(Group.ADMIN)
	@Path("advent addDay <player> <day>")
	void adventAddDay(Pugmas20User user, int day) {
		user.getFoundDays().add(day);
		pugmasService.save(user);

		send("Added day " + day + " to " + user.getName());
	}

	@Path("district")
	@Description("View which district you are currently in")
	void district() {
		District district = District.of(location());
		send(PREFIX + "You are " + (district == District.UNKNOWN ? "not in a district" : "in the &e" + district.getName() + " District"));
	}

	@Permission(Group.ADMIN)
	@Path("waypoint give <day>")
	void waypointGive(int day) {
		pugmasUser.getLocatedDays().add(day);
		pugmasService.save(pugmasUser);
	}

	@Path("waypoint <day>")
	@Description("Get directions to a chest you have already found")
	void waypoint(int day) {
		if (!pugmasUser.getLocatedDays().contains(day))
			error("You have not located that chest yet");

		AdventChest adventChest = AdventChests.getAdventChest(day);
		if (adventChest == null)
			error("Advent chest is null");

		Pugmas20.showWaypoint(adventChest, player());
	}

	@Permission(Group.ADMIN)
	@Path("waypoints")
	void waypoint() {
		for (AdventChest adventChest : AdventChests.adventChestList)
			Pugmas20.showWaypoint(adventChest, player());
	}

	@HideFromHelp
	@Path("toys")
	void toys() {
		if (pugmasUser.getToyTestingStage() == QuestStage.NOT_STARTED)
			error("You cannot use this");

		player().teleportAsync(ToyTesting.getBackLocation(), TeleportCause.COMMAND);
	}

	@Path("train")
	@Permission(Group.ADMIN)
	void train() {
		if (Pugmas20Train.animating())
			error("Train is animating!");

		if (Bukkit.getTPS()[0] < 19)
			error("TPS is too low, must be 19+");

		Pugmas20Train.animate();
	}

	@Permission(Group.ADMIN)
	@Path("tree build <treeType> <id>")
	void treeSchematic(PugmasTreeType treeType, int id) {
		treeType.build(id);
	}

	@Permission(Group.ADMIN)
	@Path("tree feller <treeType> <id>")
	void treeFeller(PugmasTreeType treeType, int id) {
		treeType.feller(player(), id);
	}

	@Permission(Group.ADMIN)
	@Path("tree feller all")
	void treeFeller() {
		for (PugmasTreeType treeType : PugmasTreeType.values())
			for (Integer id : treeType.getPasters().keySet())
				treeType.feller(player(), id);
	}

	@Permission(Group.ADMIN)
	@Path("tree copy <treeType>")
	void treeCopy(PugmasTreeType treeType) {
		runCommand("/copy -m orange_wool,snow," + treeType.getAllMaterialsString());
	}

	@Permission(Group.ADMIN)
	@Path("tree save <treeType> <id>")
	void treeSave(PugmasTreeType treeType, int id) {
		runCommand("mcmd /copy -m snow," + treeType.getAllMaterialsString() + " ;; /schem save pugmas20/trees/" + treeType.name().toLowerCase() + "/" + id + " -f");
	}

	@Permission(Group.ADMIN)
	@Path("tree region <treeType> <id>")
	void treeRegion(PugmasTreeType treeType, int id) {
		ProtectedRegion region = treeType.getRegion(id);
		String command = region == null ? "define" : "redefine";
		runCommand("mcmd /here ;; rg " + command + " pugmas20_trees_" + treeType.name().toLowerCase() + "_" + id);
	}

	@Permission(Group.ADMIN)
	@Path("tree get")
	void treeGet() {
		Material logs = getTargetBlockRequired().getType();
		PugmasTreeType treeType = PugmasTreeType.of(logs);
		if (treeType == null)
			error("Pugmas Tree with logs " + camelCase(logs) + " not found");

		send(PREFIX + "You are looking at a " + camelCase(treeType) + " tree");
	}

	@Permission(Group.ADMIN)
	@Path("tree counts")
	void treeCounts() {
		int total = 0;
		JsonBuilder json = json(PREFIX + "Pugmas tree counts:");
		for (PugmasTreeType treeType : PugmasTreeType.values()) {
			Set<Integer> ids = treeType.getPasters().keySet();
			if (ids.size() == 0)
				continue;

			String collect = ids.stream().map(String::valueOf).collect(Collectors.joining(", "));
			json.newline().next("&e " + camelCase(treeType) + " &7- " + ids.size() + " &3[" + collect + "]");
			total += ids.size();
		}

		if (total == 0)
			error("No pugmas trees found");

		send(json.newline().next("&3Total: &e" + total));
	}

	@Permission(Group.ADMIN)
	@Path("kit the_mines pickaxe")
	void kitMinersPickaxe() {
		PlayerUtils.giveItem(player(), TheMines.getMinersPickaxe());
	}

	@Permission(Group.ADMIN)
	@Path("kit the_mines sieve")
	void kitMinersSieve() {
		PlayerUtils.giveItem(player(), TheMines.getMinersSieve());
	}

	@Permission(Group.ADMIN)
	@Path("kit the_mines ores")
	void kitMinersOres() {
		for (OreType oreType : OreType.values())
			PlayerUtils.giveItem(player(), oreType.getOre());
	}

	@Permission(Group.ADMIN)
	@Path("kit the_mines ingots")
	void kitMinersIngot() {
		for (OreType oreType : OreType.values())
			PlayerUtils.giveItem(player(), oreType.getIngot());
	}

	@Permission(Group.ADMIN)
	@Path("kit ornament_vendor ornaments")
	void kitOrnamentVendorOrnaments() {
		for (Ornament ornament : Ornament.values())
			PlayerUtils.giveItem(player(), ornament.getSkull());
	}

	@Permission(Group.ADMIN)
	@Path("kit ornament_vendor axe")
	void kitOrnamentVendorAxe() {
		PlayerUtils.giveItem(player(), OrnamentVendor.getLumberjacksAxe());
	}

	@Permission(Group.ADMIN)
	@Path("kit ornament_vendor logs")
	void kitOrnamentVendorLogs() {
		for (Ornament ornament : Ornament.values())
			PlayerUtils.giveItem(player(), ornament.getTreeType().getLog(64));
	}

	@Permission(Group.ADMIN)
	@Path("kit light_the_tree")
	void kitLightTheTree() {
		PlayerUtils.giveItem(player(), LightTheTree.lighter);
		PlayerUtils.giveItem(player(), LightTheTree.lighter_broken);
		PlayerUtils.giveItem(player(), LightTheTree.steel_ingot);
	}

	@Permission(Group.ADMIN)
	@Path("inventory store")
	void inventoryStore() {
		pugmasUser.storeInventory();
		pugmasService.save(pugmasUser);
		send(PREFIX + "Stored inventory");
	}

	@Permission(Group.ADMIN)
	@Path("inventory apply")
	void inventoryApply() {
		pugmasUser.applyInventory();
		pugmasService.save(pugmasUser);
	}

	@Permission(Group.ADMIN)
	@Path("npcs emeralds")
	void npcsHolograms() {
		Pugmas20.createNpcHolograms();
	}

	@Permission(Group.ADMIN)
	@Path("quests stage set <quest> <stage>")
	void questStageSet(Pugmas20QuestStageHelper quest, QuestStage stage) {
		quest.setter().accept(pugmasUser, stage);
		pugmasService.save(pugmasUser);
		send(PREFIX + "Quest stage for Quest " + camelCase(quest) + " set to " + camelCase(stage));
	}

	@Permission(Group.ADMIN)
	@Path("quests stage get <quest>")
	void questStageSet(Pugmas20QuestStageHelper quest) {
		send(PREFIX + "Quest stage for Quest " + camelCase(quest) + ": " + quest.getter().apply(pugmasUser));
	}

	@Permission(Group.ADMIN)
	@Path("quests light_the_tree setTorchesLit <int>")
	void questLightTheTreeSetLit(int lit) {
		pugmasUser.setTorchesLit(lit);
		pugmasService.save(pugmasUser);
		send(PREFIX + "Set torches lit to " + lit);
	}

	@Permission(Group.ADMIN)
	@Path("quests light_the_tree reset")
	void questLightTheTreeReset() {
		pugmasUser.resetLightTheTree();
		pugmasService.save(pugmasUser);
		send(PREFIX + "Reset Light The Tree quest variables");
	}

	@Permission(Group.ADMIN)
	@Path("quests ornament_vendor reset")
	void questOrnamentVendorReset() {
		pugmasUser.getOrnamentTradeCount().clear();
		pugmasService.save(pugmasUser);
		send(PREFIX + "Reset Ornament Vendor quest variables");
	}

	@Permission(Group.ADMIN)
	@Path("quests ornament_vendor reloadHeads")
	void questOrnamentVendorReloadHeads() {
		Ornament.loadHeads();
		send(PREFIX + "Reloaded Ornament Vendor heads");
	}

	@HideFromHelp
	@TabCompleteIgnore
	@Path("quests light_the_tree teleportToStart")
	void questLightTheTreeTeleportToStart() {
		if (pugmasUser.isLightingTorches())
			error("You cannot teleport during the lighting ceremony");

		player().teleportAsync(LightTheTree.getResetLocation(), TeleportCause.COMMAND);
		send(PREFIX + "Teleported to ceremony start");
	}

	@Permission(Group.ADMIN)
	@Path("debug <player>")
	void debugUser(@Arg("self") Pugmas20User user) {
		send(user.toString());
	}

}
