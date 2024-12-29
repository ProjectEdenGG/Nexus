package gg.projecteden.nexus.features.events.y2021.pugmas21;

import com.sk89q.worldedit.regions.Region;
import gg.projecteden.api.common.utils.RandomUtils;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.features.events.y2021.pugmas21.advent.Pugmas21Advent;
import gg.projecteden.nexus.features.events.y2021.pugmas21.advent.Pugmas21AdventAnimation;
import gg.projecteden.nexus.features.events.y2021.pugmas21.advent.Pugmas21AdventMenu;
import gg.projecteden.nexus.features.events.y2021.pugmas21.models.*;
import gg.projecteden.nexus.features.events.y2021.pugmas21.models.Pugmas21MultiModelStructure.Model;
import gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC;
import gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21QuestItem;
import gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21QuestLine;
import gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21QuestTask;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.*;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.pugmas21.Advent21Config;
import gg.projecteden.nexus.models.pugmas21.Advent21Config.AdventPresent;
import gg.projecteden.nexus.models.pugmas21.Advent21ConfigService;
import gg.projecteden.nexus.models.pugmas21.Pugmas21User;
import gg.projecteden.nexus.models.pugmas21.Pugmas21UserService;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldEditUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@HideFromWiki
@NoArgsConstructor
@Permission(Group.ADMIN)
//@Redirect(from = "/advent", to = "/pugmas21 advent")
//@Redirect(from = "/district", to = "/pugmas21 district")
//@Redirect(from = "/waypoint", to = "/pugmas21 advent waypoint")
public class Pugmas21Command extends CustomCommand implements Listener {
	public String PREFIX = Pugmas21.PREFIX;
	private final Pugmas21UserService service = new Pugmas21UserService();
	private Pugmas21User user;

	private final Advent21ConfigService adventService = new Advent21ConfigService();
	private final Advent21Config adventConfig = adventService.get0();

	private final String timeLeft = Timespan.of(Pugmas21.EPOCH).format();

	public Pugmas21Command(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Override
	public String getPrefix() {
		return Pugmas21.PREFIX;
	}

	@Path
	void pugmas() {
		if (Pugmas21.isBeforePugmas() && !isStaff())
			error("Soon™ (" + timeLeft + ")");

		if (!user.isFirstVisit())
			error("You need to take the Pugmas train at Spawn to unlock this warp.");

		player().teleportAsync(Pugmas21.warp, TeleportCause.COMMAND);
	}

	@Path("randomizePresents")
	@Permission(Group.ADMIN)
	@Description("Randomizes the presents in your selection")
	void randomizePresents() {
		List<Instrument> instruments = List.of(Instrument.DIDGERIDOO, Instrument.PLING);

		WorldEditUtils WEUtils = new WorldEditUtils(player());
		Region selection = WEUtils.getPlayerSelection(player());

		for (Block block : WEUtils.getBlocks(selection)) {
			if (block.getType() != Material.NOTE_BLOCK)
				continue;

			NoteBlock noteBlock = (NoteBlock) block.getBlockData();
			noteBlock.setInstrument(RandomUtils.randomElement(instruments));
			noteBlock.setNote(new Note(RandomUtils.randomInt(0, 24)));
			block.setBlockData(noteBlock);
		}
	}

//	@Path("train spawn <model>")
//	@Permission(Group.ADMIN)
//	@Description("Spawn a train armor stand")
//	void train_spawn(@Arg(min = 1) int model) {
//		gg.projecteden.nexus.features.events.models.Train.armorStand(model - 1, location());
//	}

//	@Path("train spawn all")
//	@Permission(Group.ADMIN)
//	@Description("Spawn all train armor stands")
//	void train_spawn_all() {
//		gg.projecteden.nexus.features.events.models.Train.builder()
//			.location(location())
//			.direction(player().getFacing())
//			.build()
//			.spawnArmorStands();
//	}

	@Path("train start default")
	@Permission(Group.ADMIN)
	@Description("Start a moving train")
	void train_start_default() {
		Pugmas21Train.getDefault().build().start();
	}

	@Path("train start here")
	@Permission(Group.ADMIN)
	@Description("Start a moving train")
	void train_start_here(
		@Arg(".3") @Switch double speed,
		@Arg("60") @Switch int seconds
	) {
		gg.projecteden.nexus.features.events.models.Train.builder()
			.location(location())
			.direction(player().getFacing())
			.speed(speed)
			.seconds(seconds)
			.test(true)
			.build()
			.start();
	}

	@Path("trainBackground start")
	@Permission(Group.ADMIN)
	void trainBackground_start() {
		Pugmas21TrainBackground.start();
	}

	@Path("trainBackground stop")
	@Permission(Group.ADMIN)
	void trainBackground_stop() {
		Pugmas21TrainBackground.stop();
	}

	private Pugmas21MultiModelStructure getBalloonStructure() {
		final AtomicInteger i = new AtomicInteger();
		final int baseModelId = CustomMaterial.PUGMAS21_HOT_AIR_BALLOON_1.getModelId();

		return Pugmas21MultiModelStructure.builder()
			.from(location().subtract(BlockFace.UP.getDirection().multiply(1.5)))
			.add(Map.of(BlockFace.UP, 0), baseModelId + i.getAndIncrement())
			.add(Map.of(BlockFace.UP, 1), baseModelId + i.getAndIncrement())
			.add(Map.of(BlockFace.UP, 2), baseModelId + i.getAndIncrement())
			.cardinal(direction -> new Model(Map.of(BlockFace.UP, 1, direction, 1), baseModelId + i.getAndIncrement()).direction(direction))
			.cardinal(direction -> new Model(Map.of(BlockFace.UP, 2, direction, 1), baseModelId + i.getAndIncrement()).direction(direction));
	}

	@Path("balloon spawn")
	@Permission(Group.ADMIN)
	void balloon_spawn() {
		getBalloonStructure().spawn();
	}

	@Path("balloon move [--seconds]")
	@Permission(Group.ADMIN)
	void balloon_move(@Arg("20") @Switch int seconds) {
		final Pugmas21MultiModelStructure structure = getBalloonStructure().spawn();

		player().setGravity(false);
		int taskId = Tasks.repeat(0, 1, () -> {
			final Vector west = BlockFace.WEST.getDirection().multiply(.1);
			player().setVelocity(west);
			for (Model model : structure.getModels()) {
				EntityUtils.forcePacket(model.getArmorStand());
				model.getArmorStand().teleport(model.getArmorStand().getLocation().add(west));
			}
		});

		Tasks.wait(TickTime.SECOND.x(seconds), () -> {
			Tasks.cancel(taskId);
			player().setGravity(true);
		});
	}

	@Path("candycane cannon")
	@Permission(Group.ADMIN)
	void candycane_cannon() {
		giveItem(Pugmas21CandyCaneCannon.getItem().build());
	}

	@Path("district")
	@Description("View which district you are currently in")
	void district() {
		Pugmas21District district = Pugmas21District.of(location());
		if (district == null)
			error("You must be in Pugmas to run this command");

		send(PREFIX + "You are " + (district == Pugmas21District.UNKNOWN ? "not in a district" : "in the &e" + district.getFullName()));
	}

	@Path("advent animation [--twice] [--height1] [--length1] [--particle1] [--ticks1] [--height2] [--length2] [--particle2] [--ticks2] [--randomMax] [--day]")
	@Permission(Group.ADMIN)
	void advent_animation(
		@Arg("false") @Switch boolean twice,
		@Arg("0.25") @Switch double length1,
		@Arg("0.5") @Switch double height1,
		@Arg("crit") @Switch Particle particle1,
		@Arg("40") @Switch int ticks1,
		@Arg("0.25") @Switch double length2,
		@Arg("0.25") @Switch double height2,
		@Arg("crit") @Switch Particle particle2,
		@Arg("40") @Switch int ticks2,
		@Arg("40") @Switch int randomMax,
		@Arg("1") @Switch int day
	) {
		final Pugmas21AdventAnimation animation = Pugmas21AdventAnimation.builder()
			.location(location())
			.length1(length1)
			.height1(height1)
			.particle1(particle1)
			.ticks1(ticks1)
			.length2(length2)
			.height2(height2)
			.particle2(particle2)
			.ticks2(ticks2)
			.randomMax(randomMax)
			.player(player())
			.present(Advent21Config.get().get(day))
			.build();

		if (twice)
			animation.openTwice();
		else
			animation.open();
	}

	@Path("advent")
	@Description("Open the advent calender")
	void advent(
		@Arg(value = "0", permission = Group.ADMIN) @Switch int day,
		@Arg(value = "30", permission = Group.ADMIN) @Switch int frameTicks
	) {
		verifyDate();

		LocalDate date = Pugmas21.TODAY;
		if (date.isBefore(Pugmas21.EPOCH) || day > 0)
			date = Pugmas21.EPOCH.plusDays(day - 1);

		new Pugmas21AdventMenu(user, date, frameTicks).open(player());
	}

	@Path("advent waypoint <day>")
	@Description("Get directions to a present you've already found")
	void advent_waypoint(int day) {
		verifyDate();

		if (!user.advent().hasFound(day))
			error("You have not found day &e#" + day);

		Pugmas21Advent.glow(user, day);
	}

	@Path("advent nearest")
	@Permission(Group.ADMIN)
	void advent_nearest() {
		AdventPresent nearestPresent = Collections.min(adventConfig.getPresents(), Comparator.comparing(present -> distanceTo(present).get()));

		if (nearestPresent == null)
			error("None found");

		send("Nearest: #" + nearestPresent.getDay());
	}

	@Path("advent waypoints")
	@Permission(Group.ADMIN)
	void advent_waypoints() {
		for (AdventPresent present : adventConfig.getPresents())
			user.advent().glow(present);

		send(PREFIX + "Made " + adventConfig.getPresents().size() + " presents glow");
	}

	@Path("advent get <day>")
	@Permission(Group.ADMIN)
	void advent_get(@Arg(min = 1, max = 25) int day) {
		giveItem(Advent21Config.get().get(day).getItem().build());
	}

	@Path("advent stats opened <day>")
	@Permission(Group.ADMIN)
	void advent_stats_opened(@Arg(min = 1, max = 25) int day) {
		send(PREFIX + "Players who opened &e#" + day);
		send(service.getAll().stream()
			.filter(user -> user.advent().hasCollected(day))
			.map(user -> Nerd.of(user).getColoredName()).collect(Collectors.joining("&f, ")));
	}

	@Path("advent stats found <day>")
	@Permission(Group.ADMIN)
	void advent_stats_found(@Arg(min = 1, max = 25) int day) {
		send(PREFIX + "Players who found &e#" + day);
		send(service.getAll().stream()
			.filter(user -> user.advent().hasFound(day))
			.map(user -> Nerd.of(user).getColoredName()).collect(Collectors.joining("&f, ")));
	}

	@Path("advent config updateItems")
	@Permission(Group.ADMIN)
	void advent_updateItems() {
		Pugmas21Advent.updateItems();

		send(PREFIX + "updated items");
	}

	@Path("advent config setLootOrigin")
	@Permission(Group.ADMIN)
	void advent_lootOrigin() {
		final Block block = getTargetBlockRequired();

		adventConfig.setLootOrigin(block.getLocation());
		adventService.save(adventConfig);

		send(PREFIX + "lootOrigin configured");
	}

	@Path("advent config set <day>")
	@Permission(Group.ADMIN)
	void advent_config(@Arg(min = 1, max = 25) int day) {
		final Block block = getTargetBlockRequired();
		if (block.getType() != Material.BARRIER)
			error("You must be looking at a barrier");

		adventConfig.set(day, block.getLocation());
		adventService.save(adventConfig);

		send(PREFIX + "Advent day #" + day + " configured");
	}

	@Path("advent tp <day>")
	@Permission(Group.ADMIN)
	void advent_tp(int day) {
		user.advent().teleportAsync(adventConfig.get(day));
		send(PREFIX + "Teleported to day #" + day);
	}

	@Path("simulate today <day>")
	@Permission(Group.ADMIN)
	void simulate_today(int day) {
		Pugmas21.TODAY = Pugmas21.EPOCH.plusDays(day - 1);
		send(PREFIX + "Simulating date &e" + TimeUtils.shortDateFormat(Pugmas21.TODAY));
	}

	@Path("simulate today reset")
	@Permission(Group.ADMIN)
	void simulate_today_reset() {
		Pugmas21.TODAY = LocalDate.now();
		send(PREFIX + "Simulating date &e" + TimeUtils.shortDateFormat(Pugmas21.TODAY));
	}

	@Path("quest debug <task>")
	void quest_debug(Pugmas21QuestTask task) {
		send(String.valueOf(task.builder()));
	}

	@Path("quest start <quest>")
	void quest_start(Pugmas21QuestLine quest) {
		user.setQuestLine(quest);
		quest.start(player());
		send(PREFIX + "Quest line " + quest + " activated");
	}

	@Path("quest npc tp <quest>")
	void quest_npc_tp(Pugmas21NPC pugmasNPC) {
		final NPC npc = CitizensUtils.getNPC(pugmasNPC.getNpcId());
		final Entity entity = npc.getEntity();
		if (entity != null)
			player().teleportAsync(entity.getLocation(), TeleportCause.COMMAND);
		else if (npc.getStoredLocation() != null)
			player().teleportAsync(npc.getStoredLocation(), TeleportCause.COMMAND);
		else
			error("Could not determine location of NPC");
	}

	@Path("quest item <item>")
	void quest_item(Pugmas21QuestItem item) {
		giveItem(item.get());
	}

	/*
	@EventHandler
	public void onNPCRightClick(NPCRightClickEvent event) {
		final Pugmas21NPC npc = Pugmas21NPC.of(event.getNPC());
		if (npc == null)
			return;

		Quester.of(event.getClicker()).interact(npc);
		event.setCancelled(true);

		String npcName = npc.getName();
		String message = StringUtils.colorize("&3" + npcName + " &7> &f" + Pugmas21.getGenericGreeting());
		event.getClicker().sendMessage(message);
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		final Pugmas21Entity entity = Pugmas21Entity.of(event.getRightClicked());
		if (entity == null)
			return;

		Quester.of(event.getPlayer()).interact(entity);
		event.setCancelled(true);
	}
	*/

	private void verifyDate() {
		if (!isAdmin()) {
			if (Pugmas21.isBeforePugmas())
				error("Soon™ (" + timeLeft + ")");

			if (Pugmas21.isPastPugmas())
				error("Next year!");
		}
	}

}
