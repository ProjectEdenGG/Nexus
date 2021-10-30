package gg.projecteden.nexus.features.events.y2021.pugmas21;

import gg.projecteden.nexus.features.events.y2021.pugmas21.advent.AdventAnimation;
import gg.projecteden.nexus.features.events.y2021.pugmas21.advent.AdventMenu;
import gg.projecteden.nexus.features.events.y2021.pugmas21.models.CandyCaneCannon;
import gg.projecteden.nexus.features.events.y2021.pugmas21.models.District;
import gg.projecteden.nexus.features.events.y2021.pugmas21.models.MultiModelStructure;
import gg.projecteden.nexus.features.events.y2021.pugmas21.models.MultiModelStructure.Model;
import gg.projecteden.nexus.features.events.y2021.pugmas21.models.Train;
import gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21Entity;
import gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC;
import gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21QuestItem;
import gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21QuestLine;
import gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21QuestTask;
import gg.projecteden.nexus.features.quests.users.Quester;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.pugmas21.Advent21Config;
import gg.projecteden.nexus.models.pugmas21.Advent21Config.AdventPresent;
import gg.projecteden.nexus.models.pugmas21.Advent21ConfigService;
import gg.projecteden.nexus.models.pugmas21.Pugmas21User;
import gg.projecteden.nexus.models.pugmas21.Pugmas21UserService;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

import java.time.LocalDate;
import java.util.Map;

import static gg.projecteden.utils.TimeUtils.shortDateFormat;

@NoArgsConstructor
@Permission("group.staff")
public class Pugmas21Command extends CustomCommand implements Listener {
	public String PREFIX = Pugmas21.PREFIX;
	private final Pugmas21UserService service = new Pugmas21UserService();
	private Pugmas21User user;

	private final Advent21ConfigService adventService = new Advent21ConfigService();
	private final Advent21Config adventConfig = adventService.get0();

	public Pugmas21Command(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Override
	public String getPrefix() {
		return Pugmas21.PREFIX;
	}

	@Path("train spawn <model>")
	@Description("Spawn a train armor stand")
	void train_spawn(int model) {
		Train.armorStand(model, location());
	}

	@Path("train spawn all")
	@Description("Spawn all train armor stands")
	void train_spawn_all() {
		Train.builder()
			.location(location())
			.direction(player().getFacing())
			.build()
			.spawnArmorStands();
	}

	@Path("train start default")
	@Description("Start a moving train")
	void train_start_default() {
		Train.getDefault().build().start();
	}

	@Path("train start here")
	@Description("Start a moving train")
	void train_start_here(
		@Arg(".3") @Switch double speed,
		@Arg("60") @Switch int seconds
	) {
		Train.builder()
			.location(location())
			.direction(player().getFacing())
			.speed(speed)
			.seconds(seconds)
			.test(true)
			.build()
			.start();
	}

	private MultiModelStructure getBalloonStructure() {
		return MultiModelStructure.builder()
			.from(location().subtract(BlockFace.UP.getDirection().multiply(1.5)))
			.add(Map.of(BlockFace.UP, 0), 31)
			.add(Map.of(BlockFace.UP, 1), 32)
			.add(Map.of(BlockFace.UP, 2), 33)
			.cardinal(direction -> new Model(Map.of(BlockFace.UP, 1, direction, 1), 34).direction(direction))
			.cardinal(direction -> new Model(Map.of(BlockFace.UP, 2, direction, 1), 35).direction(direction));
	}

	@Path("balloon spawn")
	void balloon_spawn() {
		getBalloonStructure().spawn();
	}

	@Path("balloon move [--seconds]")
	void balloon_move(@Arg("20") @Switch int seconds) {
		final MultiModelStructure structure = getBalloonStructure().spawn();

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
	void candycane_cannon() {
		giveItem(CandyCaneCannon.getItem().build());
	}

	@Path("district")
	@Description("View which district you are currently in")
	void district() {
		District district = District.of(location());
		if (district == null)
			error("You must be in Pugmas to run this command");

		send(PREFIX + "You are " + (district == District.UNKNOWN ? "not in a district" : "in the &e" + district.getFullName()));
	}

	@Path("advent animation [--twice] [--height1] [--length1] [--particle1] [--ticks1] [--height2] [--length2] [--particle2] [--ticks2] [--randomMax]")
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
		@Arg("40") @Switch int randomMax
	) {
		final AdventAnimation animation = AdventAnimation.builder()
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
			.build();

		if (twice)
			animation.openTwice();
		else
			animation.open();
	}

	@Path("advent")
	@Description("Open the advent calender")
	void advent(
		@Arg(value = "0", permission = "group.admin") @Switch int day,
		@Arg(value = "30", permission = "group.admin") @Switch int frameTicks
	) {
		LocalDate date = Pugmas21.TODAY;
		if (date.isBefore(Pugmas21.EPOCH) || day > 0)
			date = Pugmas21.EPOCH.plusDays(day - 1);

		new AdventMenu(user, date, frameTicks).open(player());
	}

	@Path("advent waypoint <day>")
	@Description("Get directions to a present you've already found")
	void advent_waypoint(int day) {
		if (!user.advent().hasFound(day))
			error("You have not found day &e#" + day);

		user.advent().locate(adventConfig.get(day));
	}

	@Path("advent waypoints")
	@Permission("group.admin")
	void advent_waypoints() {
		for (AdventPresent present : adventConfig.getPresents())
			user.advent().glow(present);

		send(PREFIX + "Made " + adventConfig.getPresents().size() + " presents glow");
	}

	@Path("advent config set <day>")
	@Permission("group.admin")
	void advent_config(@Arg(min = 1, max = 25) int day) {
		final Block block = getTargetBlockRequired();
		if (block.getType() != Material.BARRIER)
			error("You must be looking at a barrier");

		adventConfig.set(day, block.getLocation());
		adventService.save(adventConfig);
		send(PREFIX + "Advent day #" + day + " configured");
	}

	@Path("advent tp <day>")
	@Permission("group.admin")
	void advent_tp(int day) {
		user.advent().teleportAsync(adventConfig.get(day));
		send(PREFIX + "Teleported to day #" + day);
	}

	@Path("simulate today <day>")
	@Permission("group.admin")
	void simulate_today(int day) {
		Pugmas21.TODAY = Pugmas21.EPOCH.plusDays(day - 1);
		send(PREFIX + "Simulating date &e" + shortDateFormat(Pugmas21.TODAY));
	}

	@Path("simulate today reset")
	@Permission("group.admin")
	void simulate_today_reset() {
		Pugmas21.TODAY = LocalDate.now();
		send(PREFIX + "Simulating date &e" + shortDateFormat(Pugmas21.TODAY));
	}

	@Path("quest debug <task>")
	void quest_debug(Pugmas21QuestTask task) {
		send(String.valueOf(task.get()));
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

	@EventHandler
	public void onNPCRightClick(NPCRightClickEvent event) {
		final Pugmas21NPC npc = Pugmas21NPC.of(event.getNPC());
		if (npc == null)
			return;

		Quester.of(event.getClicker()).interact(npc);
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		final Pugmas21Entity entity = Pugmas21Entity.of(event.getRightClicked());
		if (entity == null)
			return;

		Quester.of(event.getPlayer()).interact(entity);
		event.setCancelled(true);
	}

}
