package me.pugabyte.bncore.features.holidays.bearfair20.commands;

import me.pugabyte.bncore.features.holidays.bearfair20.fairgrounds.Interactables;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests;
import me.pugabyte.bncore.features.warps.commands._WarpCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.bearfair.BearFairService;
import me.pugabyte.bncore.models.bearfair.BearFairUser;
import me.pugabyte.bncore.models.warps.Warp;
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

import static me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests.itemLore;

public class BearFairCommand extends _WarpCommand {

	public BearFairCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void bearfair() {
		send(PREFIX + "Coming soon!");
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.BEAR_FAIR;
	}

	@Override
	@Path("warps list [filter]")
	@Permission("group.moderator")
	public void list(@Arg(tabCompleter = Warp.class) String filter) {
		super.list(filter);
	}

	@Override
	@Path("warps set <name>")
	@Permission("group.moderator")
	public void set(@Arg(tabCompleter = Warp.class) String name) {
		player();
		super.set(name);
	}

	@Override
	@Path("warps (rm|remove|delete|del) <name>")
	@Permission("group.moderator")
	public void delete(Warp warp) {
		super.delete(warp);
	}

	@Override
	@Path("warps (teleport|tp) <name>")
	@Permission("group.moderator")
	public void teleport(Warp warp) {
		player();
		super.teleport(warp);
	}

	@Override
	@Path("warps <name>")
	@Permission("group.moderator")
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
	@Permission("group.moderator")
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

	@Path("quests reset")
	@Permission("group.admin")
	void questReset() {
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player());
		//
		user.getEasterEggsLocs().clear();
		//
		user.setQuest_Main_Step(0);
		user.setQuest_Main_Start(false);
		user.setQuest_Main_Finish(false);
		//
		user.setQuest_SDU_Step(0);
		user.setQuest_SDU_Start(false);
		user.setQuest_SDU_Finish(false);
		//
		user.setQuest_MGN_Step(0);
		user.getArcadePieces().clear();
		user.setQuest_MGN_Start(false);
		user.setQuest_MGN_Finish(false);
		//
		user.setQuest_Halloween_Step(0);
		user.setQuest_Halloween_Start(false);
		user.setQuest_Halloween_Finish(false);
		//
		user.setQuest_Pugmas_Step(0);
		user.setQuest_Pugmas_Start(false);
		user.setQuest_Pugmas_Finish(false);
		//
		service.save(user);
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

	@Path("quests start sdu")
	@Permission("group.admin")
	void startSDUQuest() {
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player());
		user.setQuest_SDU_Start(true);
		service.save(user);
	}

	@Path("quests start mgn")
	@Permission("group.admin")
	void startMGNQuest() {
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player());
		user.setQuest_MGN_Start(true);
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

	@Path("quests giveMGNItems")
	@Permission("group.admin")
	void giveMGNItems() {
		ItemBuilder cpu = new ItemBuilder(Material.IRON_TRAPDOOR).lore(itemLore).amount(1).name("CPU");
		ItemBuilder processor = new ItemBuilder(Material.DAYLIGHT_DETECTOR).lore(itemLore).amount(1).name("Processor");
		ItemBuilder memoryCard = new ItemBuilder(Material.IRON_INGOT).lore(itemLore).amount(1).name("Memory Card");
		ItemBuilder motherboard = new ItemBuilder(Material.GREEN_CARPET).lore(itemLore).amount(1).name("Motherboard");
		ItemBuilder powerSupply = new ItemBuilder(Material.BLAST_FURNACE).lore(itemLore).amount(1).name("Power Supply");
		ItemBuilder speaker = new ItemBuilder(Material.NOTE_BLOCK).lore(itemLore).amount(1).name("Speaker");
		ItemBuilder hardDrive = new ItemBuilder(Material.HOPPER_MINECART).lore(itemLore).amount(1).name("Hard Drive");
		ItemBuilder diode = new ItemBuilder(Material.REPEATER).lore(itemLore).amount(1).name("Diode");
		ItemBuilder joystick = new ItemBuilder(Material.LEVER).lore(itemLore).amount(1).name("Joystick");
		List<ItemStack> arcadePieces = Arrays.asList(cpu.build(), processor.build(), memoryCard.build(), motherboard.build(),
				powerSupply.build(), speaker.build(), hardDrive.build(), diode.build(), joystick.build());
		ItemStack solderingIron = new ItemBuilder(Material.END_ROD).lore(itemLore).amount(1).name("Soldering Iron").build();
		ItemStack fakeMotherBoard = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).lore(itemLore).amount(1).name("Motherboard").build();

		for (ItemStack arcadePiece : arcadePieces)
			Utils.giveItem(player(), arcadePiece);
		Utils.giveItem(player(), solderingIron);
		Utils.giveItem(player(), fakeMotherBoard);
	}
}
