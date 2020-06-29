package me.pugabyte.bncore.features.holidays.bearfair20.commands;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.features.holidays.bearfair20.fairgrounds.Interactables;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.HalloweenIsland;
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
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;

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

		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player());

		if (user.isFirstVisit())
			error("To unlock the warp, you must first travel to Bear Fair aboard the space yacht at spawn");

		runCommandAsOp("bearfair tp bearfair");
	}

	@Path("quests <text>")
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

	// Admin/CommandBlock Commands

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

	@Override
	@Path("warps (rm|remove|delete|del) <name>")
	@Permission("group.admin")
	public void delete(Warp warp) {
		super.delete(warp);
	}

	@Override
	@Path("warps (teleport|tp) <name>")
	@Permission("group.admin")
	public void teleport(Warp warp) {
		player();
		super.teleport(warp);
	}

	@Override
	@Path("warps <name>")
	@Permission("group.admin")
	public void tp(Warp warp) {
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

	@Path("clearData")
	@Permission("group.admin")
	void clearData() {
		MenuUtils.ConfirmationMenu.builder()
				.onConfirm(e -> Tasks.async(() -> {
					BearFairService service = new BearFairService();
					BearFairUser user = service.get(player());
					service.delete(user);
				}))
				.open(player());
	}

//	@Path("clearDatabase")
//	@Permission("group.admin")
//	void clearDatabase() {
//		MenuUtils.ConfirmationMenu.builder()
//				.onConfirm(e -> Tasks.async(() -> {
//					BearFairService service = new BearFairService();
//					service.deleteAll();
//				}))
//				.open(player());
//	}

	@Path("quests info")
	@Permission("group.admin")
	void questInfo() {
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player());
		//
		send("====");
		send("Found Easter Eggs: " + user.getEasterEggsLocs().size());
		//
		send();
		send("Main Step: " + user.getQuest_Main_Step());
		send("Main Start: " + user.isQuest_Main_Start());
		send("Main Finish: " + user.isQuest_Main_Finish());
		//
		send();
		send("SDU Step: " + user.getQuest_SDU_Step());
		send("SDU Start: " + user.isQuest_SDU_Start());
		send("SDU Finish: " + user.isQuest_SDU_Finish());
		//
		send();
		send("MGN Step: " + user.getQuest_MGN_Step());
		send("MGN Start: " + user.isQuest_MGN_Start());
		send("MGN Finish: " + user.isQuest_MGN_Finish());
		//
		send();
		send("Halloween Step: " + user.getQuest_Halloween_Step());
		send("Halloween Start: " + user.isQuest_Halloween_Start());
		send("Halloween Finish: " + user.isQuest_Halloween_Finish());
		//
		send();
		send("Pugmas Switched: " + user.isQuest_Pugmas_Switched());
		send("Pugmas Presents: " + user.getPresentLocs().toString());
		send("Pugmas Step: " + user.getQuest_Pugmas_Step());
		send("Pugmas Start: " + user.isQuest_Pugmas_Start());
		send("Pugmas Finish: " + user.isQuest_Pugmas_Finish());
		send("====");
	}

	@Path("quests start main")
	@Permission("group.admin")
	void startMainQuest() {
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player());
		user.setQuest_Main_Start(true);
		service.save(user);
	}

	@Path("quests start halloween")
	@Permission("group.admin")
	void startHalloweenQuest() {
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player());
		user.setQuest_Halloween_Start(true);
		service.save(user);
	}

	@Path("quests start pugmas")
	@Permission("group.admin")
	void startPugmasQuest() {
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player());
		user.setQuest_Pugmas_Start(true);
		service.save(user);
	}

	@Path("giveQuestItems")
	@Permission("group.admin")
	void questItems() {
		List<ItemStack> questItems = Collections.singletonList(HalloweenIsland.atticKey);
		Utils.giveItems(player(), questItems);
	}

	Map<UUID, Integer> bfpImport = new HashMap<UUID, Integer>() {{
		put(UUID.fromString("0baaebf5-cfb0-431a-b625-465c64e694f1"), 125);
		put(UUID.fromString("0f54da29-2d6e-48d9-9107-9dc89272e0ee"), 10);
		put(UUID.fromString("3eafb33e-fcf9-4cee-8f94-478a2a91da23"), 125);
		put(UUID.fromString("04d92bf6-1212-4712-8d68-b681d0a65a9b"), 50);
		put(UUID.fromString("4d808e9b-e37c-41a2-b96c-ea5c83d3c031"), 25);
		put(UUID.fromString("5ced9758-8268-46f8-9e12-54731dc7ba0f"), 125);
		put(UUID.fromString("5d5165a1-b430-488d-8642-4d849fa526da"), 80);
		put(UUID.fromString("6c555ed6-f3b7-4afe-b634-74dde6789c9d"), 690);
		put(UUID.fromString("6dbb7b77-a68e-448d-a5bc-fb531a7fe22d"), 95);
		put(UUID.fromString("7d87b6c3-bfc0-4972-9086-3dcc0da9d865"), 125);
		put(UUID.fromString("7f2132d8-bf94-4dda-b9a8-8c233c25f764"), 25);
		put(UUID.fromString("8b06ff94-84f3-4733-beb1-7cf9f1db46f4"), 315);
		put(UUID.fromString("9b51c965-e41c-4c9d-9d8c-2bd1b7e421e0"), 660);
		put(UUID.fromString("9cd780e5-119c-420f-9884-0027616e6709"), 430);
		put(UUID.fromString("9d619bd9-eaa3-4507-8b7c-3edccb53588a"), 250);
		put(UUID.fromString("22b0cc70-2573-4107-a5ae-e2acbe01d0cd"), 125);
		put(UUID.fromString("30b12912-f307-4db9-a340-57b18fb0dd92"), 5);
		put(UUID.fromString("56cb00fd-4738-47bc-be08-cb7c4f9a5a94"), 50);
		put(UUID.fromString("60c65f26-f6bd-4fba-8fe4-2ae2bd52a6bd"), 130);
		put(UUID.fromString("67fa2c3b-149c-4a2c-bfe6-cf7eaa45db63"), 1720);
		put(UUID.fromString("71f57e55-9c7b-4c0e-825b-01d2025c3952"), 525);
		put(UUID.fromString("86d7e0e2-c95e-4f22-8f99-a6e83b398307"), 1210);
		put(UUID.fromString("88f9f7f6-7703-49bf-ad83-a4dec7e8022c"), 1015);
		put(UUID.fromString("257fbdcb-4577-4429-9f64-01f34377797f"), 50);
		put(UUID.fromString("308da2ed-3ed7-4444-81cf-ba9b1b3c90aa"), 45);
		put(UUID.fromString("0383cc62-834d-4882-abfb-b333d989e052"), 50);
		put(UUID.fromString("424ee16f-aac7-4907-960c-311c933e5ad2"), 175);
		put(UUID.fromString("499a1e70-d91a-4b55-bc5e-eb02824d838c"), 125);
		put(UUID.fromString("562f342c-fed5-467f-b067-f62b78f9375a"), 330);
		put(UUID.fromString("1869e704-8b76-4bfb-bdab-447c629e83f2"), 45);
		put(UUID.fromString("2116cd0a-3a0c-4038-80ac-7ea753868b43"), 65);
		put(UUID.fromString("5302a862-5db4-461e-bec3-01e3941cd298"), 5);
		put(UUID.fromString("6723aec2-590b-40ca-9abc-77a630d11c97"), 130);
		put(UUID.fromString("9595d8c8-78a3-4163-b67e-799fc348a97f"), 50);
		put(UUID.fromString("12682fbf-5e99-47b0-adb9-7a6916d87fa7"), 25);
		put(UUID.fromString("47100b7b-6e7b-4211-ac71-ca7356489b6c"), 125);
		put(UUID.fromString("55666e97-7756-442a-9a5c-a27685cde3b4"), 250);
		put(UUID.fromString("56478eec-98b5-4be5-bf7f-df8d87bcb35b"), 50);
		put(UUID.fromString("77966ca3-ac85-44b2-bcb0-b7c5f9342e86"), 1560);
		put(UUID.fromString("922109ae-4b05-4446-af21-6d9baa5046cf"), 15);
		put(UUID.fromString("01477970-77f8-4e0a-bcb4-2d38f9da8f88"), 125);
		put(UUID.fromString("1524830c-6eaf-4469-9cb5-6e9312c9aa59"), 170);
		put(UUID.fromString("2349275a-2620-4148-9f4b-3eb2e40e9577"), 425);
		put(UUID.fromString("31926843-c131-4962-a608-ebb785cf6f8d"), 275);
		put(UUID.fromString("64888722-2cc5-4e8d-bca3-b8a40e9451e6"), 125);
		put(UUID.fromString("a42a0743-df33-4c87-9747-be1d92537ebf"), 125);
		put(UUID.fromString("a82a64a5-5a72-4e04-811b-b972bb8dad0e"), 125);
		put(UUID.fromString("a556cb97-e03f-4a37-a253-33923e7ecb34"), 115);
		put(UUID.fromString("a4274d94-10f2-4663-af3b-a842c7ec729c"), 15);
		put(UUID.fromString("ac758647-a3a5-4186-b2df-8e4f67f06c4e"), 75);
		put(UUID.fromString("acb8a46c-ff15-4127-812c-0a7d3485fdd7"), 50);
		put(UUID.fromString("b5ab7830-bfd9-4ce4-b2eb-746a7605614d"), 200);
		put(UUID.fromString("b83bae78-83d6-43a0-9316-014a0a702ab2"), 55);
		put(UUID.fromString("b260f9d1-9c03-431a-b6bc-4e3f1c6e060c"), 5);
		put(UUID.fromString("b299e2d2-4011-4968-a55c-638d9c37664b"), 355);
		put(UUID.fromString("be973c5a-8d5b-4400-9114-401e9bf05159"), 125);
		put(UUID.fromString("c4f09cd1-4028-44a6-9e7b-bae40e1c27c4"), 150);
		put(UUID.fromString("c6e6f780-5dcc-4238-a8b6-c4baed531450"), 125);
		put(UUID.fromString("c32a246c-f8b4-4ac9-b036-c205e065e981"), 130);
		put(UUID.fromString("c66d5b17-c5eb-4a50-8cd4-b38c421acd8d"), 125);
		put(UUID.fromString("c90d21e1-b8cf-414d-a6ff-c4dc86ffaaf9"), 20);
		put(UUID.fromString("d1de9ca8-78f6-4aae-87a1-8c112f675f12"), 5);
		put(UUID.fromString("d4d02750-ee29-475d-a60f-ce0e04ea3a34"), 190);
		put(UUID.fromString("d34f717e-1955-462e-a735-7a15622643e9"), 75);
		put(UUID.fromString("d1729990-0ad4-4db8-8a95-779128e9fa1a"), 1375);
		put(UUID.fromString("ddae863e-7b8d-454d-b8dd-498f28c84f51"), 130);
		put(UUID.fromString("e8b6ccb9-bb20-4a2e-9c04-feb7ca2ce7eb"), 50);
		put(UUID.fromString("e9e07315-d32c-4df7-bd05-acfe51108234"), 2140);
		put(UUID.fromString("ecde30cc-c84d-4b52-ac50-b0bef78d9149"), 300);
		put(UUID.fromString("ef3308f3-e2bd-41d5-a319-f6939ee39658"), 55);
		put(UUID.fromString("f2ca26f9-1cc3-4fe9-9574-d118e34785c7"), 1325);
		put(UUID.fromString("f3b4bf6a-0714-44fa-a925-4b714df16ba1"), 300);
		put(UUID.fromString("f3bd4b1d-0041-4130-8c70-6d7a777aaf22"), 125);
		put(UUID.fromString("f33ace68-5f09-4c24-b24b-8f3e5afe9dfd"), 135);
		put(UUID.fromString("fa8a62d4-e069-4626-afff-5fc5a6fb8a16"), 5);
		put(UUID.fromString("fce1fe67-9514-4117-bcf6-d0c49ca0ba41"), 15);
	}};

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
				points = dailyMap.get(LocalDate.now());

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
		String plural = points == 1 ? " point" : " points";
		send(PREFIX + "&e" + points + plural + " &3given to &e" + user.getOfflinePlayer().getName());
	}

	@Path("points take <player> <points>")
	@Permission("group.admin")
	public void pointsTake(BearFairUser user, int points) {
		user.takePoints(points);
		service.save(user);
		String plural = points == 1 ? " point" : " points";
		send(PREFIX + "&e" + points + plural + " &3taken from &e" + user.getOfflinePlayer().getName());
	}

	@Path("points set <player> <points>")
	@Permission("group.admin")
	public void pointsSet(BearFairUser user, int points) {
		user.setTotalPoints(points);
		service.save(user);
		String plural = points == 1 ? " point" : " points";
		send(PREFIX + "&3set &e" + user.getOfflinePlayer().getName() + "&3 to &e" + points + plural);
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

	@Path("points import")
	@Permission("group.admin")
	public void importPoints() {
		bfpImport.forEach(((uuid, points) -> ((BearFairUser) service.get(uuid)).setTotalPoints(points)));
		send(PREFIX + "Points successfully imported");
	}

	@ConverterFor(BearFairUser.class)
	BearFairUser convertToBearFairUser(String value) {
		return new BearFairService().get(convertToOfflinePlayer(value));
	}

	@TabCompleterFor(BearFairUser.class)
	List<String> tabCompleteBearFairUser(String value) {
		return tabCompletePlayer(value);
	}

}
