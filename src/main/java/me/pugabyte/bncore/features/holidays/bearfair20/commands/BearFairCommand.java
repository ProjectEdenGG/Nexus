package me.pugabyte.bncore.features.holidays.bearfair20.commands;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.features.holidays.bearfair20.fairgrounds.Interactables;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Island;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.MainIsland;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.PugmasIsland;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.warps.commands._WarpCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.bearfair.BearFairService;
import me.pugabyte.bncore.models.bearfair.BearFairUser;
import me.pugabyte.bncore.models.bearfair.BearFairUser.BFPointSource;
import me.pugabyte.bncore.models.warps.Warp;
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;
import static me.pugabyte.bncore.features.holidays.bearfair20.islands.HalloweenIsland.atticKey;
import static me.pugabyte.bncore.features.holidays.bearfair20.islands.HalloweenIsland.basketItem;
import static me.pugabyte.bncore.features.holidays.bearfair20.islands.MainIsland.*;
import static me.pugabyte.bncore.features.holidays.bearfair20.islands.MinigameNightIsland.arcadePieces;
import static me.pugabyte.bncore.features.holidays.bearfair20.islands.PugmasIsland.presentItem;
import static me.pugabyte.bncore.features.holidays.bearfair20.islands.SummerDownUnderIsland.*;

@Redirect(from = {"/bfp", "bfpoints", "/bearfairpoints"}, to = "/bearfair points")
public class BearFairCommand extends _WarpCommand {
	private final BearFairService service = new BearFairService();

	public BearFairCommand(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.BEAR_FAIR;
	}

	@Path
	void bearfair() {
		if (!BearFair20.allowWarp)
			error("Warp is disabled");

		BearFairUser user = service.get(player());

		if (user.isFirstVisit())
			error("To unlock the warp, you must first travel to Bear Fair aboard the space yacht at spawn");

		runCommandAsOp("bearfair tp bearfair");
	}

	@Path("quests giveAllQuestItem")
	void giveQuestItems() {
		// MAIN
		Utils.giveItems(player(), Arrays.asList(honeyStroopWafel, stroofWafel, blessedHoneyBottle, relic, ancientPickaxe, rareFlower, specialPrize));
		// SDU
		Utils.giveItems(player(), Arrays.asList(anzacBiscuit, goldenSyrup, peanuts));
		// PUGMAS
		Utils.giveItem(player(), presentItem);
		// MGN
		arcadePieces.forEach(piece -> Utils.giveItem(player(), piece.build()));
		// HALLOWEEN
		Utils.giveItems(player(), Arrays.asList(atticKey, basketItem));
	}

	@Path("quests npc <text>")
	void questsSwitchQuest(String string) {
		ProtectedRegion pugmasRegion = WGUtils.getProtectedRegion(BearFair20.getRegion() + "_" + PugmasIsland.class.getAnnotation(Island.Region.class).value());
		ProtectedRegion mainRegion = WGUtils.getProtectedRegion(BearFair20.getRegion() + "_" + MainIsland.class.getAnnotation(Island.Region.class).value());
		if (!WGUtils.getRegionsAt(player().getLocation()).contains(pugmasRegion) && !WGUtils.getRegionsAt(player().getLocation()).contains(mainRegion))
			return;
		switch (string) {
			// Main
			case "accept_witch":
				MainIsland.acceptWitchQuest(player());
				break;
			// Pugmas
			case "switch_mayor":
				PugmasIsland.switchQuest(player(), true);
				break;
			case "switch_grinch":
				PugmasIsland.switchQuest(player(), false);
				break;
			case "accept_mayor":
				PugmasIsland.acceptQuest(player(), true);
				break;
			case "accept_grinch":
				PugmasIsland.acceptQuest(player(), false);
				break;
		}
	}

	@Path("quests stats [player]")
	public void questStats(@Arg("self") Player player) {
		BearFairUser user = service.get(player);

		int treasureChests = user.getEasterEggsLocs().size();
		String questStatus = "&cIncomplete";
		if (user.isQuest_Main_Finish())
			questStatus = "&aComplete";

		String done = "&a✔";
		String notDone = "&c✖";

		send("");
		send("&3" + player.getName() + "'s Quest Stats:");
		send("&3Treasure Chests: &e" + treasureChests + "&3/&e15");
		send("&3Quest Progress: " + questStatus);
		if (questStatus.equalsIgnoreCase("&aComplete")) {
			send("");
			return;
		}

		String status_main = user.getQuest_Main_Step() == 3 ? done : notDone;
		String status_mgn = user.isQuest_MGN_Finish() ? done : notDone;
		String status_pugmas = user.isQuest_Pugmas_Finish() ? done : notDone;
		String status_halloween = user.isQuest_Halloween_Finish() ? done : notDone;
		String status_sdu = user.isQuest_SDU_Finish() ? done : notDone;

		send(status_main + " &7- &eHoney Stroopwafel");
		send(status_mgn + " &7- &eArcade Token");
		send(status_pugmas + " &7- &ePresent");
		send(status_halloween + " &7- &eHalloween Candy Basket");
		send(status_sdu + " &7- &eANZAC Biscuit");
		send("");
	}

	// Admin Commands

	// Warp Overrides
	@Override
	@Path("<name>")
	@Permission("group.admin")
	public void tp(Warp warp) {
		error("disabled");
	}

	@Override
	@Path("(teleport|tp|warp) <name>")
	@Permission("group.admin")
	public void teleport(Warp warp) {
		error("disabled");
	}

	@Override
	@Path("(rm|remove|delete|del) <name>")
	@Permission("group.admin")
	public void delete(Warp warp) {
		error("disabled");
	}

	@Path("quests info")
	@Permission("group.admin")
	public void topTreasureChests() {
		List<BearFairUser> all = service.getAll();
		int started = (int) all.stream().filter(BearFairUser::isQuest_Main_Start).count();
		int finished = (int) all.stream().filter(BearFairUser::isQuest_Main_Finish).count();

		send("");
		send(PREFIX + "&3Players Started/Finished: " + started + "\\" + finished);
		send(PREFIX + "&3Found EasterEggs:");
		all.stream()
				.filter(user -> user.getEasterEggsLocs().size() > 0)
				.sorted(Comparator.comparing((BearFairUser user) -> user.getEasterEggsLocs().size()).reversed())
				.forEach(user -> send("&3" + user.getOfflinePlayer().getName() + " &7- &e" + user.getEasterEggsLocs().size()));
		send("");
	}

	@Override
	@Path("warps list [filter]")
	@Permission("group.admin")
	public void list(@Arg(tabCompleter = Warp.class) String filter) {
		super.list(filter);
	}

	@Override
	@Path("warps set <name>")
	@Permission("group.admin")
	public void set(@Arg(tabCompleter = Warp.class) String name) {
		player();
		super.set(name);
	}

	@Path("warps (rm|remove|delete|del) <name>")
	@Permission("group.admin")
	public void warpsDelete(Warp warp) {
		super.delete(warp);
	}

	@Path("warps (teleport|tp) <name>")
	@Permission("group.admin")
	public void warpsTeleport(Warp warp) {
		player();
		super.teleport(warp);
	}

	@Path("warps <name>")
	@Permission("group.admin")
	public void warpsTP(Warp warp) {
		player();
		super.tp(warp);
	}

	@Path("warps tp nearest")
	public void teleportNearest() {
		player();
		getNearestWarp(player().getLocation()).ifPresent(warp -> warp.teleport(player()));
	}

	@Override
	@Path("warps nearest")
	@Permission("group.admin")
	public void nearest() {
		super.nearest();
	}

	@Path("recipes")
	@Permission("group.admin")
	void recipes() {
		runCommandAsConsole("minecraft:recipe take " + player().getName() + " bncore:custom_bearfair_anzac_biscuit");
		runCommandAsConsole("minecraft:recipe give " + player().getName() + " bncore:custom_bearfair_anzac_biscuit");
	}

	@Path("strengthTest")
	@Permission("group.admin")
	void strengthTest() {
		commandBlock();
		Interactables.strengthTest();
	}

	@Path("clearData [player]")
	@Permission("group.admin")
	void clearData(@Arg("self") Player player) {
		MenuUtils.ConfirmationMenu.builder()
				.onConfirm(e -> Tasks.async(() -> {
					BearFairUser user = service.get(player);
					service.delete(user);
				}))
				.open(player());
	}


	// Point Commands

	@Path("points [player]")
	public void points(@Arg("self") BearFairUser user) {
		if (player().equals(user.getOfflinePlayer()))
			send(PREFIX + "&3Total: &e" + user.getTotalPoints());
		else
			send(PREFIX + "&3" + user.getOfflinePlayer().getName() + "'s Total: &e" + user.getTotalPoints());
	}

	@Path("points daily [player]")
	public void pointsDaily(@Arg("self") BearFairUser user) {
		if (player().equals(user.getOfflinePlayer()))
			send(PREFIX + "&3Daily Points:");
		else
			send(PREFIX + "&3" + user.getOfflinePlayer().getName() + "'s Daily Points:");

		for (BFPointSource pointSource : BFPointSource.values()) {
			Map<LocalDate, Integer> dailyMap = user.getPointsReceivedToday().get(pointSource);
			int points = 0;
			if (dailyMap != null)
				points = dailyMap.getOrDefault(LocalDate.now(), 0);

			int dailyMax = BearFairUser.DAILY_SOURCE_MAX;
			String sourceColor = points == dailyMax ? "&a" : "&3";
			String sourceName = StringUtils.camelCase(pointSource.name());
			send(" " + sourceColor + sourceName + " &7- &e" + points + "&3/&e" + dailyMax);
		}
	}

	@Path("points give <player> <points>")
	@Permission("group.admin")
	public void pointsGive(BearFairUser user, int points) {
		user.givePoints(points);
		service.save(user);
		send(PREFIX + "&e" + points + plural(" point", points) + " &3given to &e" + user.getOfflinePlayer().getName());
	}

	@Path("points take <player> <points>")
	@Permission("group.admin")
	public void pointsTake(BearFairUser user, int points) {
		user.takePoints(points);
		service.save(user);
		send(PREFIX + "&e" + points + plural(" point", points) + " &3taken from &e" + user.getOfflinePlayer().getName());
	}

	@Path("points set <player> <points>")
	@Permission("group.admin")
	public void pointsSet(BearFairUser user, int points) {
		user.setTotalPoints(points);
		service.save(user);
		send(PREFIX + "&3set &e" + user.getOfflinePlayer().getName() + "&3 to &e" + points + plural(" point", points));
	}

	@Path("points reset <player>")
	@Permission("group.admin")
	public void pointsReset(BearFairUser user) {
		user.setTotalPoints(0);
		user.getPointsReceivedToday().clear();
		service.save(user);
	}

	@Path("points top [page]")
	public void pointsTop(@Arg("1") int page) {
		List<BearFairUser> results = service.getTopPoints(page);
		if (results.size() == 0)
			error("&cNo results on page " + page);

		send("");
		send(PREFIX + (page > 1 ? "&3Page " + page : ""));
		int i = (page - 1) * 10 + 1;
		for (BearFairUser user : results)
			send("&3" + i++ + " &e" + user.getOfflinePlayer().getName() + " &7- " + user.getTotalPoints());
	}

	@ConverterFor(BearFairUser.class)
	BearFairUser convertToBearFairUser(String value) {
		return service.get(convertToOfflinePlayer(value));
	}

	@TabCompleterFor(BearFairUser.class)
	List<String> tabCompleteBearFairUser(String value) {
		return tabCompletePlayer(value);
	}


	// Command Blocks
	@Path("smite")
	@Permission("group.admin")
	public void smite() {
		commandBlock();
		BlockCommandSender sender = (BlockCommandSender) event.getSender();
		Location loc = sender.getBlock().getLocation();
		World world = loc.getWorld();
		if (world != null)
			world.strikeLightningEffect(loc);
		MainIsland.witchQuestCraft();
	}

	@Path("yachtHorn")
	@Permission("group.admin")
	public void yachtHorn() {
		commandBlock();
		BlockCommandSender sender = (BlockCommandSender) event.getSender();
		Location loc = sender.getBlock().getLocation();
		World world = loc.getWorld();
		if (world == null) return;
		world.playSound(loc, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 4F, 0.1F);
		Tasks.wait(Time.SECOND.x(2), () -> world.playSound(loc, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 4F, 0.1F));
	}

	@Path("moveCollector")
	@Permission("group.admin")
	public void moveCollector() {
		commandBlock();
		BFQuests.moveCollector();
	}

}
