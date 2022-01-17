package gg.projecteden.nexus.features.events.y2020.bearfair20.commands;

import gg.projecteden.nexus.features.events.y2020.bearfair20.BearFair20;
import gg.projecteden.nexus.features.warps.commands._WarpCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.bearfair20.BearFair20UserService;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.models.warps.Warps.Warp;
import lombok.NoArgsConstructor;
import org.bukkit.event.Listener;

@NoArgsConstructor
public class BearFair20Command extends _WarpCommand implements Listener {
	private final BearFair20UserService service = new BearFair20UserService();

	public BearFair20Command(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.BEAR_FAIR20;
	}

	@Path
	void bearfair() {
		if (!BearFair20.allowWarp)
			error("Warp is disabled");

//		BearFair20User user = service.get(player());
//		if (user.isFirstVisit())
//			error("To unlock the warp, you must first travel to Bear Fair aboard the space yacht at spawn");

		teleport(getWarpType().get("bearfair"));
	}

	@Path("gallery")
	void warpToGallery() {
		teleport(getWarpType().get("gallery"));
	}

	@Path("store")
	void warpToStore() {
		teleport(getWarpType().get("store"));
	}

//	@Path("quests giveAllQuestItem")
//	void giveQuestItems() {
//		// MAIN
//		PlayerUtils.giveItems(player(), Arrays.asList(honeyStroopWafel, stroofWafel, blessedHoneyBottle, relic, ancientPickaxe, rareFlower, specialPrize));
//		// SDU
//		PlayerUtils.giveItems(player(), Arrays.asList(anzacBiscuit, goldenSyrup, peanuts));
//		// PUGMAS
//		PlayerUtils.giveItem(player(), presentItem);
//		// MGN
//		arcadePieces.forEach(piece -> PlayerUtils.giveItem(player(), piece.build()));
//		// HALLOWEEN
//		PlayerUtils.giveItems(player(), Arrays.asList(atticKey, basketItem));
//	}
//
//	@Path("quests npc <text>")
//	void questsSwitchQuest(String string) {
//		if (!BearFair20.enableQuests) return;
//
//		ProtectedRegion pugmasRegion = worldguard().getProtectedRegion(IslandType.PUGMAS.get().getRegion());
//		ProtectedRegion mainRegion = worldguard().getProtectedRegion(IslandType.MAIN.get().getRegion());
//		if (!worldguard().getRegionsAt(location()).contains(pugmasRegion) && !worldguard().getRegionsAt(location()).contains(mainRegion))
//			return;
//		switch (string) {
//			// Main
//			case "accept_witch":
//				MainIsland.acceptWitchQuest(player());
//				break;
//			// Pugmas
//			case "switch_mayor":
//				PugmasIsland.switchQuest(player(), true);
//				break;
//			case "switch_grinch":
//				PugmasIsland.switchQuest(player(), false);
//				break;
//			case "accept_mayor":
//				PugmasIsland.acceptQuest(player(), true);
//				break;
//			case "accept_grinch":
//				PugmasIsland.acceptQuest(player(), false);
//				break;
//		}
//	}
//
//	@Path("quests stats [player]")
//	public void questStats(@Arg("self") Player player) {
//		BearFair20User user = service.get(player);
//
//		int treasureChests = user.getEasterEggsLocs().size();
//		String questStatus = "&cIncomplete";
//		if (user.isQuest_Main_Finish())
//			questStatus = "&aComplete";
//
//		String done = "&a✔";
//		String notDone = "&c✖";
//
//		send("");
//		send("&3" + player.getName() + "'s Quest Stats:");
//		send("&3Treasure Chests: &e" + treasureChests + "&3/&e15");
//		send("&3Quest Progress: " + questStatus);
//		if (questStatus.equalsIgnoreCase("&aComplete")) {
//			send("");
//			return;
//		}
//
//		String status_main = user.getQuest_Main_Step() == 3 ? done : notDone;
//		String status_mgn = user.isQuest_MGN_Finish() ? done : notDone;
//		String status_pugmas = user.isQuest_Pugmas_Finish() ? done : notDone;
//		String status_halloween = user.isQuest_Halloween_Finish() ? done : notDone;
//		String status_sdu = user.isQuest_SDU_Finish() ? done : notDone;
//
//		send(status_main + " &7- &eHoney Stroopwafel");
//		send(status_mgn + " &7- &eArcade Token");
//		send(status_pugmas + " &7- &ePresent");
//		send(status_halloween + " &7- &eHalloween Candy Basket");
//		send(status_sdu + " &7- &eANZAC Biscuit");
//		send("");
//	}
//
//	// Point Commands
//
//	@Path("points [player]")
//	public void points(@Arg("self") BearFair20User user) {
//		if (isSelf(user))
//			send(PREFIX + "&3Total: &e" + user.getTotalPoints());
//		else
//			send(PREFIX + "&3" + user.getNickname() + "'s Total: &e" + user.getTotalPoints());
//	}
//
//	@Path("points daily [player]")
//	public void pointsDaily(@Arg("self") BearFair20User user) {
//		if (isSelf(user))
//			send(PREFIX + "&3Daily Points:");
//		else
//			send(PREFIX + "&3" + user.getName() + "'s Daily Points:");
//
//		for (BF20PointSource pointSource : BF20PointSource.values()) {
//			Map<LocalDate, Integer> dailyMap = user.getPointsReceivedToday().get(pointSource);
//			int points = 0;
//			if (dailyMap != null)
//				points = dailyMap.getOrDefault(LocalDate.now(), 0);
//
//			int dailyMax = BearFair20User.DAILY_SOURCE_MAX;
//			String sourceColor = points == dailyMax ? "&a" : "&3";
//			String sourceName = StringUtils.camelCase(pointSource.name());
//			send(" " + sourceColor + sourceName + " &7- &e" + points + "&3/&e" + dailyMax);
//		}
//	}
//
//	@Path("points pay <player> <points>")
//	public void pointsPay(BearFair20User toUser, int points) {
//		BearFair20User fromUser = service.get(player());
//		if (isSelf(toUser))
//			error("You cannot pay yourself");
//
//		fromUser.takePoints(points);
//		toUser.givePoints(points);
//
//		fromUser.sendMessage(PREFIX + "&e" + points + " BFP &3has been sent to &e" + toUser.getNickname());
//		if (toUser.isOnline())
//			toUser.sendMessage(PREFIX + "&e" + points + " BFP &3has been received from &e" + fromUser.getNickname());
//
//		service.save(fromUser);
//		service.save(toUser);
//	}
//
//	@Path("points give <player> <points>")
//	@Permission(Group.ADMIN)
//	public void pointsGive(BearFair20User user, int points) {
//		user.givePoints(points);
//		service.save(user);
//		send(PREFIX + "&e" + points + plural(" point", points) + " &3given to &e" + user.getNickname());
//	}
//
//	@Path("points take <player> <points>")
//	@Permission(Group.ADMIN)
//	public void pointsTake(BearFair20User user, int points) {
//		user.takePoints(points);
//		service.save(user);
//		send(PREFIX + "&e" + points + plural(" point", points) + " &3taken from &e" + user.getNickname());
//	}
//
//	@Path("points set <player> <points>")
//	@Permission(Group.ADMIN)
//	public void pointsSet(BearFair20User user, int points) {
//		user.setTotalPoints(points);
//		service.save(user);
//		send(PREFIX + "&3set &e" + user.getNickname() + "&3 to &e" + points + plural(" point", points));
//	}
//
//	@Path("points reset <player>")
//	@Permission(Group.ADMIN)
//	public void pointsReset(BearFair20User user) {
//		user.setTotalPoints(0);
//		user.getPointsReceivedToday().clear();
//		service.save(user);
//	}
//
//	@Path("points top [page]")
//	public void pointsTop(@Arg("1") int page) {
//		List<BearFair20User> results = service.getTopPoints(page);
//		if (results.size() == 0)
//			error("&cNo results on page " + page);
//
//		send("");
//		send(PREFIX + (page > 1 ? "&3Page " + page : ""));
//		int i = (page - 1) * 10 + 1;
//		for (BearFair20User user : results)
//			send("&3" + i++ + " &e" + user.getNickname() + " &7- " + user.getTotalPoints());
//	}
//
//	// Admin Commands
//
//	@Path("quests info")
//	@Permission(Group.ADMIN)
//	public void topTreasureChests() {
//		List<BearFair20User> all = service.getAll();
//		int started = (int) all.stream().filter(BearFair20User::isQuest_Main_Start).count();
//		int finished = (int) all.stream().filter(BearFair20User::isQuest_Main_Finish).count();
//
//		send("");
//		send(PREFIX + "&3Players Started/Finished: " + started + "\\" + finished);
//		send(PREFIX + "&3Found EasterEggs:");
//		all.stream()
//				.filter(user -> user.getEasterEggsLocs().size() > 0)
//				.sorted(Comparator.comparing((BearFair20User user) -> user.getEasterEggsLocs().size()).reversed())
//				.forEach(user -> send("&3" + user.getNickname() + " &7- &e" + user.getEasterEggsLocs().size()));
//		send("");
//	}

	@Override
	@Path("warps list [filter]")
	@Permission(Group.ADMIN)
	public void list(@Arg(tabCompleter = Warp.class) String filter) {
		super.list(filter);
	}

	@Override
	@Path("warps set <name>")
	@Permission(Group.ADMIN)
	public void set(@Arg(tabCompleter = Warp.class) String name) {
		super.set(name);
	}

	@Path("warps (rm|remove|delete|del) <name>")
	@Permission(Group.ADMIN)
	public void delete(Warp warp) {
		super.delete(warp);
	}

	@Path("warps (teleport|tp) <name>")
	@Permission(Group.ADMIN)
	public void teleport(Warp warp) {
		super.teleport(warp);
	}

	@Path("warps <name>")
	@Permission(Group.ADMIN)
	public void tp(Warp warp) {
		super.tp(warp);
	}

	@TabCompleteIgnore(permission = Group.ADMIN)
	@Path("warps tp nearest")
	public void teleportNearest() {
		super.teleportNearest();
	}

	@Override
	@Path("warps nearest")
	@Permission(Group.ADMIN)
	public void nearest() {
		super.nearest();
	}

//	@Path("recipes")
//	@Permission(Group.ADMIN)
//	void recipes() {
//		runCommandAsConsole("minecraft:recipe take " + name() + " nexus:custom_bearfair_anzac_biscuit");
//		runCommandAsConsole("minecraft:recipe give " + name() + " nexus:custom_bearfair_anzac_biscuit");
//	}

//	@Path("strengthTest")
//	@Permission(Group.ADMIN)
//	void strengthTest() {
//		commandBlock();
//		Interactables.strengthTest();
//	}

//	@Async
//	@Confirm
//	@Path("clearData [player]")
//	@Permission(Group.ADMIN)
//	void clearData(@Arg("self") Player player) {
//		BearFair20User user = service.get(player);
//		service.delete(user);
//	}

	// Command Blocks
//	@Path("smite")
//	@Permission(Group.ADMIN)
//	public void smite() {
//		if (!BearFair20.enableQuests) return;
//		commandBlock();
//		BlockCommandSender sender = (BlockCommandSender) event.getSender();
//		Location loc = sender.getBlock().getLocation();
//		World world = loc.getWorld();
//		if (world != null)
//			world.strikeLightningEffect(loc);
//		MainIsland.witchQuestCraft();
//	}

//	@Path("yachtHorn")
//	@Permission(Group.ADMIN)
//	public void yachtHorn() {
//		commandBlock();
//		BlockCommandSender sender = (BlockCommandSender) event.getSender();
//		Location loc = sender.getBlock().getLocation();
//		World world = loc.getWorld();
//		if (world == null) return;
//		world.playSound(loc, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 4F, 0.1F);
//		Tasks.wait(TickTime.SECOND.x(2), () -> world.playSound(loc, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 4F, 0.1F));
//	}

//	@Path("moveCollector")
//	@Permission(Group.ADMIN)
//	public void moveCollector() {
//		commandBlock();
//		BFQuests.moveCollector();
//	}

}
