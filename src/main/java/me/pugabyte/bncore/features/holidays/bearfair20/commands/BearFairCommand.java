package me.pugabyte.bncore.features.holidays.bearfair20.commands;

import com.google.common.base.Strings;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleteIgnore;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.bearfair.BearFairService;
import me.pugabyte.bncore.models.bearfair.BearFairUser;
import me.pugabyte.bncore.models.bearfair.BearFairUser.BFPointSource;
import me.pugabyte.bncore.models.warps.Warp;
import me.pugabyte.bncore.models.warps.WarpService;
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldEditUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;
import static me.pugabyte.bncore.features.holidays.bearfair20.islands.HalloweenIsland.atticKey;
import static me.pugabyte.bncore.features.holidays.bearfair20.islands.HalloweenIsland.basketItem;
import static me.pugabyte.bncore.features.holidays.bearfair20.islands.MainIsland.*;
import static me.pugabyte.bncore.features.holidays.bearfair20.islands.MinigameNightIsland.arcadePieces;
import static me.pugabyte.bncore.features.holidays.bearfair20.islands.PugmasIsland.presentItem;
import static me.pugabyte.bncore.features.holidays.bearfair20.islands.SummerDownUnderIsland.*;
import static me.pugabyte.bncore.utils.StringUtils.stripColor;

@NoArgsConstructor
@Redirect(from = {"/bfp", "bfpoints", "/bearfairpoints"}, to = "/bearfair points")
public class BearFairCommand extends _WarpCommand implements Listener {
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

		teleport(new WarpService().get("bearfair", WarpType.BEAR_FAIR));
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
	public void delete(Warp warp) {
		super.delete(warp);
	}

	@Path("warps (teleport|tp) <name>")
	@Permission("group.admin")
	public void teleport(Warp warp) {
		player();
		super.teleport(warp);
	}

	@Path("warps <name>")
	@Permission("group.admin")
	public void tp(Warp warp) {
		player();
		super.tp(warp);
	}

	@TabCompleteIgnore(permission = "group.admin")
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

	// Custom Maps

	@EventHandler
	public void onClickSign(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Location loc = player.getLocation();

		if (!loc.getWorld().getName().toLowerCase().contains("bearfair")) return;
		if (Utils.isNullOrAir(event.getClickedBlock())) return;
		if (!MaterialTag.SIGNS.isTagged(event.getClickedBlock().getType())) return;

		Sign sign = (Sign) event.getClickedBlock().getState();
		String prefix = stripColor(sign.getLine(0));
		if (!prefix.equalsIgnoreCase("[Buy Painting]")) return;

		String title = sign.getLine(2);
		if (!sign.getLine(3).equals(""))
			title += " " + sign.getLine(3);

		String price = stripColor(sign.getLine(1));

		player.sendMessage("(TODO) Buying " + title + " for " + price);
	}

	@Path("store maps reload")
	@Permission("group.admin")
	public void storeMapsReload() {
		reloadMaps();
		send(PREFIX + "Loaded " + maps.size() + " maps");
	}

	@Path("store maps get <map...>")
	@Permission("group.admin")
	void storeGetMap(BearFairStoreMap map) {
		Utils.giveItem(player(), map.getSplatterMap());
	}

	@ConverterFor(BearFairStoreMap.class)
	BearFairStoreMap convertToBearFairStoreMap(String value) {
		return maps.get(value);
	}

	@TabCompleterFor(BearFairStoreMap.class)
	List<String> tabCompleteBearFairStoreMap(String filter) {
		return maps.keySet().stream()
				.filter(id -> id.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}

	@Data
	@AllArgsConstructor
	private static class BearFairStoreMap {
		private String id;
		private ItemStack splatterMap;
	}

	private static final Map<String, BearFairStoreMap> maps = new HashMap<>();

	static {
		reloadMaps();
	}

	private static void reloadMaps() {
		try {
			maps.clear();
			WorldEditUtils WEUtils = new WorldEditUtils(Bukkit.getWorld("bearfair"));

			for (Block block : WEUtils.getBlocks(WEUtils.getWorldGuardUtils().getRegion("maps"))) {
				try {
					if (!MaterialTag.SIGNS.isTagged(block.getType())) continue;

					Sign sign = (Sign) block.getState();
					String id = (sign.getLine(0).trim() + " " + sign.getLine(1).trim()).trim();
					if (Strings.isNullOrEmpty(id)) continue;

					Block chest = block.getRelative(BlockFace.DOWN);
					if (!(chest.getState() instanceof Chest)) continue;

					Chest inv = (Chest) chest.getState();
					ItemStack map = inv.getBlockInventory().getContents()[0].clone();

					if (map.getItemMeta().hasLore()) {
						String[] split = map.getItemMeta().getDisplayName().split("-");
						String name = String.join("-", Arrays.copyOfRange(split, 1, split.length));
						ItemBuilder.setName(map, "&6" + id + " &8-" + name);
						ItemBuilder.removeLoreLine(map, 1);
					}
					maps.put(id, new BearFairStoreMap(id, map));
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

}
