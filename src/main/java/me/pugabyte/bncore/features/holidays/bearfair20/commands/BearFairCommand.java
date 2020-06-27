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
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.bearfair.BearFairService;
import me.pugabyte.bncore.models.bearfair.BearFairUser;
import me.pugabyte.bncore.models.warps.Warp;
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;

public class BearFairCommand extends _WarpCommand {

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
	void questsPugmasSwitchQuest(String string) {
		ProtectedRegion region = WGUtils.getProtectedRegion(BearFair20.getRegion() + "_" + PugmasIsland.class.getAnnotation(Island.Region.class).value());
		if (!WGUtils.getRegionsAt(player().getLocation()).contains(region)) return;
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

	@Path("clearDatabase")
	@Permission("group.admin")
	void clearDatabase() {
		MenuUtils.ConfirmationMenu.builder()
				.onConfirm(e -> Tasks.async(() -> {
					BearFairService service = new BearFairService();
					service.deleteAll();
				}))
				.open(player());
	}

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
		send("MGN Pieces: " + user.getArcadePieces().toString());
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
}
