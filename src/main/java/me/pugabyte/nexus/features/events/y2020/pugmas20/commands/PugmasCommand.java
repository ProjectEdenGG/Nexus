package me.pugabyte.nexus.features.events.y2020.pugmas20.commands;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.events.models.QuestStage;
import me.pugabyte.nexus.features.events.y2020.pugmas20.AdventChests;
import me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20;
import me.pugabyte.nexus.features.events.y2020.pugmas20.Train;
import me.pugabyte.nexus.features.events.y2020.pugmas20.menu.AdventMenu;
import me.pugabyte.nexus.features.events.y2020.pugmas20.models.AdventChest;
import me.pugabyte.nexus.features.events.y2020.pugmas20.models.AdventChest.District;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.LightTheTree;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.OrnamentVendor;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.OrnamentVendor.Ornament;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.OrnamentVendor.PugmasTreeType;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.Quests.Pugmas20Quest;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.Quests.Pugmas20QuestStageHelper;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.TheMines;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.TheMines.OreType;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Description;
import me.pugabyte.nexus.framework.commands.models.annotations.HideFromHelp;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.eventuser.EventUser;
import me.pugabyte.nexus.models.eventuser.EventUserService;
import me.pugabyte.nexus.models.pugmas20.Pugmas20Service;
import me.pugabyte.nexus.models.pugmas20.Pugmas20User;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20.isBeforePugmas;
import static me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20.isPastPugmas;
import static me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20.isSecondChance;
import static me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20.showWaypoint;

@Aliases("pugmas")
@NoArgsConstructor
@Redirect(from = "/advent", to = "/pugmas advent")
@Redirect(from = "/district", to = "/pugmas district")
@Redirect(from = "/waypoint", to = "/pugmas waypoint")
public class PugmasCommand extends CustomCommand implements Listener {
	private final String timeLeft = StringUtils.timespanDiff(Pugmas20.openingDay);
	private final Pugmas20Service service = new Pugmas20Service();
	private Pugmas20User user;

	public PugmasCommand(CommandEvent event) {
		super(event);
		PREFIX = Pugmas20.PREFIX;
		if (isPlayer())
			user = service.get(player());
	}

	@Path()
	void pugmas() {
		send("Soon™ (" + timeLeft + ")");
	}

	@Path("progress [player] [day]")
	@Description("View your event progress")
	void progress(@Arg("self") Pugmas20User user, @Arg(min = 1, max = 25, permission = "group.staff") Integer day) {
		LocalDateTime now = LocalDateTime.now();
		if (day != null)
			now = now.withYear(2020).withMonth(12).withDayOfMonth(day);

		if (isBeforePugmas(now))
			now = now.withYear(2020).withMonth(12).withDayOfMonth(1);

		if (isPastPugmas(now))
			error("Next year!");

		if (isSecondChance(now))
			now = now.withYear(2020).withMonth(12).withDayOfMonth(25);

		day = now.getDayOfMonth();

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
		send(json("&f  " + advent).hover("Click to open the Advent menu").command("/pugmas advent"));

		line();
		send("&6&lQuests");

		for (Pugmas20QuestStageHelper quest : Pugmas20QuestStageHelper.values()) {
			QuestStage stage = quest.getter().apply(user);
			String instructions = Pugmas20Quest.valueOf(quest.name()).getInstructions(user, stage);

			if (stage == QuestStage.COMPLETE) {
				send("&f  &a☑ &3" + camelCase(quest) + " &7- &aComplete");
			} else if (stage == QuestStage.NOT_STARTED || stage == QuestStage.INELIGIBLE) {
				send("&f  &7☐ &3" + camelCase(quest) + " &7- &cNot started" + (instructions == null ? "" : " &7- " + instructions));
			} else  {
//				send("&f  &7☐ &3" + camelCase(quest) + " &7- &e" + (instructions == null ? "&cnull" : instructions));
				JsonBuilder json = json("&f  &7☐ &3" + camelCase(quest) + " &7- &eIn progress &7- ").group();
				send(instructions == null ? json.next("&cnull").hover(camelCase(stage)) : json.next("&7" + instructions));
			}
		}

		line();
	}

	@Path("advent [day]")
	@Description("Open the Advent menu")
	void advent(@Arg(min = 1, max = 25, permission = "group.staff") Integer day) {
		LocalDateTime now = LocalDateTime.now();
		if (day != null)
			now = now.withYear(2020).withMonth(12).withDayOfMonth(day);

		if (isBeforePugmas(now) && !player().hasPermission("group.staff"))
			error("Soon™ (" + timeLeft + ")");

		if (isPastPugmas(now))
			error("Next year!");

		if (isSecondChance(now))
			now = now.withYear(2020).withMonth(12).withDayOfMonth(25);

		AdventMenu.openAdvent(player(), now);
	}

	@Permission("group.admin")
	@Path("advent give <day>")
	void adventGive(int day) {
		AdventChests.giveAdventHead(player(), day);
	}

	@Permission("group.admin")
	@Path("advent open <day>")
	void adventOpenDay(int day) {
		AdventChests.openAdventLootInv(player(), day);
	}

	@Permission("group.admin")
	@Path("advent addDay <player> <day>")
	void adventAddDay(Pugmas20User user, int day) {
		user.getFoundDays().add(day);
		service.save(user);

		send("Added day " + day + " to " + user.getName());
	}

	@Path("district")
	@Description("View which district you are currently in")
	void district() {
		District district = District.of(player().getLocation());
		send(PREFIX + "You are " + (district == District.UNKNOWN ? "not in a district" : "in the &e" + district.getName() + " District"));
	}

	@Permission("group.admin")
	@Path("waypoint give <day>")
	void waypointGive(int day) {
		user.getLocatedDays().add(day);
		service.save(user);
	}

	@Path("waypoint <day>")
	@Description("Get directions to a chest you have already found")
	void waypoint(int day) {
		if (!user.getLocatedDays().contains(day))
			error("You have not located that chest yet");

		AdventChest adventChest = AdventChests.getAdventChest(day);
		if (adventChest == null)
			error("Advent chest is null");

		showWaypoint(adventChest, player());
	}

	@Permission("group.admin")
	@Path("waypoints")
	void waypoint() {
		for (AdventChest adventChest : AdventChests.adventChestList)
			showWaypoint(adventChest, player());
	}

	@Permission("group.admin")
	@Path("database delete [player]")
	void databaseDelete(@Arg("self") OfflinePlayer player) {
		service.delete(user);
		EventUserService eventUserService = new EventUserService();
		EventUser eventUser = eventUserService.get(player);
		eventUserService.delete(eventUser);
		send("Deleted data for " + player.getName());
	}

	@Path("train")
	@Permission("group.admin")
	void train() {
		if (Train.animating())
			error("Train is animating!");

		Train.animate();
	}

	@Permission("group.admin")
	@Path("tree build <treeType> <id>")
	void treeSchematic(PugmasTreeType treeType, int id) {
		treeType.build(id);
	}

	@Permission("group.admin")
	@Path("tree feller <treeType> <id>")
	void treeFeller(PugmasTreeType treeType, int id) {
		treeType.feller(player(), id);
	}

	@Permission("group.admin")
	@Path("tree feller all")
	void treeFeller() {
		for (PugmasTreeType treeType : PugmasTreeType.values())
			for (Integer id : treeType.getPasters().keySet())
				treeType.feller(player(), id);
	}

	@Permission("group.admin")
	@Path("tree copy <treeType>")
	void treeCopy(PugmasTreeType treeType) {
		runCommand("/copy -m orange_wool,snow," + treeType.getAllMaterialsString());
	}

	@Permission("group.admin")
	@Path("tree save <treeType> <id>")
	void treeSave(PugmasTreeType treeType, int id) {
		runCommand("mcmd /copy -m snow," + treeType.getAllMaterialsString() + " ;; /schem save pugmas20/trees/" + treeType.name().toLowerCase() + "/" + id + " -f");
	}

	@Permission("group.admin")
	@Path("tree region <treeType> <id>")
	void treeRegion(PugmasTreeType treeType, int id) {
		ProtectedRegion region = treeType.getRegion(id);
		String command = region == null ? "define" : "redefine";
		runCommand("mcmd /here ;; rg " + command + " pugmas20_trees_" + treeType.name().toLowerCase() + "_" + id);
	}

	@Permission("group.admin")
	@Path("tree get")
	void treeGet() {
		Material logs = getTargetBlockRequired().getType();
		PugmasTreeType treeType = PugmasTreeType.of(logs);
		if (treeType == null)
			error("Pugmas Tree with logs " + camelCase(logs) + " not found");

		send(PREFIX + "You are looking at a " + camelCase(treeType) + " tree");
	}

	@Permission("group.admin")
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

	@Permission("group.admin")
	@Path("kit the_mines pickaxe")
	void kitMinersPickaxe() {
		ItemUtils.giveItem(player(), TheMines.getMinersPickaxe());
	}

	@Permission("group.admin")
	@Path("kit the_mines sieve")
	void kitMinersSieve() {
		ItemUtils.giveItem(player(), TheMines.getMinersSieve());
	}

	@Permission("group.admin")
	@Path("kit the_mines ores")
	void kitMinersOres() {
		for (OreType oreType : OreType.values())
			ItemUtils.giveItem(player(), oreType.getOre());
	}

	@Permission("group.admin")
	@Path("kit the_mines ingots")
	void kitMinersIngot() {
		for (OreType oreType : OreType.values())
			ItemUtils.giveItem(player(), oreType.getIngot());
	}

	@Permission("group.admin")
	@Path("kit ornament_vendor ornaments")
	void kitOrnamentVendorOrnaments() {
		for (Ornament ornament : Ornament.values())
			ItemUtils.giveItem(player(), ornament.getSkull());
	}

	@Permission("group.admin")
	@Path("kit ornament_vendor axe")
	void kitOrnamentVendorAxe() {
		ItemUtils.giveItem(player(), OrnamentVendor.getLumberjacksAxe());
	}

	@Permission("group.admin")
	@Path("kit ornament_vendor logs")
	void kitOrnamentVendorLogs() {
		for (Ornament ornament : Ornament.values())
			ItemUtils.giveItem(player(), ornament.getTreeType().getLog(64));
	}

	@Permission("group.admin")
	@Path("kit light_the_tree")
	void kitLightTheTree() {
		ItemUtils.giveItem(player(), LightTheTree.lighter);
		ItemUtils.giveItem(player(), LightTheTree.lighter_broken);
		ItemUtils.giveItem(player(), LightTheTree.steel_ingot);
	}

	@Permission("group.admin")
	@Path("inventory store")
	void inventoryStore() {
		user.storeInventory();
		service.save(user);
		send(PREFIX + "Stored inventory");
	}

	@Permission("group.admin")
	@Path("inventory apply")
	void inventoryApply() {
		user.applyInventory();
		service.save(user);
	}

	@Permission("group.admin")
	@Path("npcs emeralds")
	void npcsHolograms() {
		Pugmas20.createNpcHolograms();
	}

	@Permission("group.admin")
	@Path("quests stage set <quest> <stage>")
	void questStageSet(Pugmas20QuestStageHelper quest, QuestStage stage) {
		quest.setter().accept(user, stage);
		service.save(user);
		send(PREFIX + "Quest stage for Quest " + camelCase(quest) + " set to " + camelCase(stage));
	}

	@Permission("group.admin")
	@Path("quests stage get <quest>")
	void questStageSet(Pugmas20QuestStageHelper quest) {
		send(PREFIX + "Quest stage for Quest " + camelCase(quest) + ": " + quest.getter().apply(user));
	}

	@Permission("group.admin")
	@Path("quests light_the_tree setTorchesLit <int>")
	void questLightTheTreeSetLit(int lit) {
		user.setTorchesLit(lit);
		service.save(user);
		send(PREFIX + "Set torches lit to " + lit);
	}

	@Permission("group.admin")
	@Path("quests light_the_tree reset")
	void questLightTheTreeReset() {
		user.resetLightTheTree();
		service.save(user);
		send(PREFIX + "Reset Light The Tree quest variables");
	}

	@Permission("group.admin")
	@Path("quests ornament_vendor reset")
	void questOrnamentVendorReset() {
		user.getOrnamentTradeCount().clear();
		service.save(user);
		send(PREFIX + "Reset Ornament Vendor quest variables");
	}

	@Permission("group.admin")
	@Path("quests ornament_vendor reloadHeads")
	void questOrnamentVendorReloadHeads() {
		Ornament.reloadHeads();
		send(PREFIX + "Reloaded Ornament Vendor heads");
	}

	@HideFromHelp
	@TabCompleteIgnore
	@Path("quests light_the_tree teleportToStart")
	// TODO PUGMAS Better wording?
	void questLightTheTreeTeleportToStart() {
		if (user.isLightingTorches())
			error("You cannot teleport during the lighting ceremony");

		player().teleport(LightTheTree.getResetLocation(), TeleportCause.COMMAND);
		send(PREFIX + "Teleported to ceremony start");
	}

	@Permission("group.admin")
	@Path("debug <player>")
	void debugUser(@Arg("self") Pugmas20User user) {
		send(user.toPrettyString());
	}

}
