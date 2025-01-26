package gg.projecteden.nexus.features.events.y2025.pugmas25;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.IEventCommand;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25.Pugmas25DeathCause;
import gg.projecteden.nexus.features.events.y2025.pugmas25.advent.Pugmas25Advent;
import gg.projecteden.nexus.features.events.y2025.pugmas25.advent.Pugmas25AdventMenu;
import gg.projecteden.nexus.features.events.y2025.pugmas25.balloons.Pugmas25BalloonEditor;
import gg.projecteden.nexus.features.events.y2025.pugmas25.balloons.Pugmas25BalloonEditorMenu;
import gg.projecteden.nexus.features.events.y2025.pugmas25.balloons.Pugmas25BalloonEditorUtils;
import gg.projecteden.nexus.features.events.y2025.pugmas25.balloons.Pugmas25BalloonManager;
import gg.projecteden.nexus.features.events.y2025.pugmas25.balloons.Pugmas25BlockReplaceBrushMenu;
import gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.Pugmas25WhacAMole;
import gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.slotmachine.Pugmas25SlotMachine;
import gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.slotmachine.Pugmas25SlotMachineReward;
import gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.slotmachine.Pugmas25SlotMachineReward.Pugmas25SlotMachineRewardType;
import gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.slotmachine.Pugmas25SlotMachineRewardMenu;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Districts;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Districts.Pugmas25District;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Geyser;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Intro;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Train;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.pugmas25.Advent25Config;
import gg.projecteden.nexus.models.pugmas25.Advent25ConfigService;
import gg.projecteden.nexus.models.pugmas25.Advent25Present;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.utils.Currency;
import gg.projecteden.nexus.utils.Currency.Price;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;

@HideFromWiki
@NoArgsConstructor
@Aliases("pugmas")
@Permission(Group.STAFF)
@Redirect(from = "/advent", to = "/pugmas25 advent")
public class Pugmas25Command extends IEventCommand implements Listener {
	public String PREFIX = Pugmas25.PREFIX;

	private final Pugmas25UserService service = new Pugmas25UserService();
	private Pugmas25User user;

	private final Advent25ConfigService adventService = new Advent25ConfigService();
	private final Advent25Config adventConfig = adventService.get0();

	public Pugmas25Command(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Override
	public String getPrefix() {
		return PREFIX;
	}

	@Override
	public EdenEvent getEdenEvent() {
		return Pugmas25.get();
	}

	@Path
	void pugmas() {
		if (!user.isVisited())
			error("You need to take the Pugmas train at Hub to unlock this warp.");

		player().teleportAsync(Pugmas25.get().warp, TeleportCause.COMMAND);
	}

	@Path("health reset")
	@Permission(Group.ADMIN)
	void health_reset() {
		Pugmas25.get().resetHealth(player());
	}

	@Path("health add")
	@Permission(Group.ADMIN)
	void health_add() {
		Pugmas25.get().addMaxHealth(player(), 2.0);
	}

	@Path("health subtract")
	@Permission(Group.ADMIN)
	void health_subtract() {
		Pugmas25.get().subtractMaxHealth(player(), 2.0);
	}

	@Path("health set <amount>")
	@Permission(Group.ADMIN)
	void health_set(@Arg(value = "20", min = 1, max = 40) int amount) {
		Pugmas25.get().setMaxHealth(player(), amount);
	}

	@Path("coins deposit <amount>")
	@Permission(Group.ADMIN)
	void coins_add(int amount) {
		Currency.COIN_POUCH.deposit(player(), Price.of(amount));
	}

	@Path("coins withdraw <amount>")
	@Permission(Group.ADMIN)
	void coins_remove(int amount) {
		Currency.COIN_POUCH.withdraw(player(), Price.of(amount), null, null);
	}

	@Path("district")
	@Description("View which district you are currently in")
	@Permission(Group.ADMIN)
	void district() {
		Pugmas25District district = Pugmas25Districts.of(player());
		send(PREFIX + "Area Designation: &e" + district.getName());
	}

	@Path("advent")
	@Description("Open the advent calender")
	void advent(
			@Arg(value = "0", permission = Group.ADMIN) @Switch int day,
			@Arg(value = "30", permission = Group.ADMIN) @Switch int frameTicks
	) {

		LocalDate date = LocalDate.now();
		if (date.isBefore(Pugmas25.get().getStart()) || day > 0)
			date = Pugmas25.get().getStart().plusDays(day - 1);

		new Pugmas25AdventMenu(user, date, frameTicks).open(player());
	}

	@Path("advent waypoint <day>")
	@Description("Get directions to a present you've already found")
	void advent_waypoint(int day) {
		if (!user.advent().hasFound(day))
			error("You have not found day &e#" + day);

		Pugmas25Advent.glow(user, day);
	}

	@Path("advent tp <day>")
	@Permission(Group.ADMIN)
	void advent_tp(int day) {
		user.advent().teleportAsync(adventConfig.get(day));
		send(PREFIX + "Teleported to day #" + day);
	}

	@Path("advent nearest")
	@Permission(Group.ADMIN)
	void advent_nearest() {
		Advent25Present nearestPresent = Collections.min(adventConfig.getPresents(), Comparator.comparing(present -> distanceTo(present).get()));

		if (nearestPresent == null)
			error("None found");

		send(PREFIX + "Nearest: #" + nearestPresent.getDay());
	}

	@Path("advent get <day>")
	@Permission(Group.ADMIN)
	void advent_get(@Arg(min = 1, max = 25) int day) {
		giveItem(Advent25Config.get().get(day).getItem().build());
	}

	@Path("advent config updateItems")
	@Permission(Group.ADMIN)
	void advent_updateItems() {
		Pugmas25Advent.updateItems();

		send(PREFIX + "updated items");
	}

	@Path("advent config setLootOrigin")
	@Permission(Group.ADMIN)
	void advent_lootOrigin() {
		final Block block = getTargetBlockRequired();

		adventConfig.setLootOrigin(block.getLocation());
		adventService.save(adventConfig);

		send(PREFIX + "lootOrigin configured at " + StringUtils.getCoordinateString(adventConfig.getLootOrigin()));
	}

	@Path("advent config setDay <day>")
	@Permission(Group.ADMIN)
	void advent_config(@Arg(min = 1, max = 25) int day) {
		final Block block = getTargetBlockRequired();
		if (block.getType() != Material.BARRIER)
			error("You must be looking at a barrier");

		adventConfig.set(day, block.getLocation());
		adventService.save(adventConfig);

		send(PREFIX + "Advent day #" + day + " configured");
	}

	@Path("death <cause>")
	@Permission(Group.ADMIN)
	void death_test(Pugmas25DeathCause deathCause) {
		Pugmas25.get().onDeath(player(), deathCause);
	}

	@Path("slotMachine rewards")
	void slotMachine_rewards() {
		new Pugmas25SlotMachineRewardMenu().open(player());
	}

	@Path("slotMachine roll")
	@Permission(Group.ADMIN)
	void slotMachine() {
		Pugmas25SlotMachine.get().start(player());
	}

	@Path("slotMachine simulateWin <reward> <type>")
	@Permission(Group.ADMIN)
	void slotMachine_reward(Pugmas25SlotMachineReward reward, Pugmas25SlotMachineRewardType type) {
		reward.give(player(), type);
	}

	@Path("slotMachine setup")
	@Permission(Group.ADMIN)
	void slotMachine_setup() {
		Pugmas25SlotMachine.get().init();
	}

	@Path("slotMachine reset")
	@Permission(Group.ADMIN)
	void slotMachine_reset() {
		Pugmas25SlotMachine.get().reset();
	}

	@Path("whacamole setup")
	@Permission(Group.ADMIN)
	void whacAMole_setup() {
		Pugmas25WhacAMole.get().init();
	}

	@Path("whacamole start")
	@Permission(Group.ADMIN)
	void whacAMole_start() {
		Pugmas25WhacAMole.get().start(player());
	}

	@Path("whacamole reset")
	@Permission(Group.ADMIN)
	void whacAMole_reset() {
		Pugmas25WhacAMole.get().reset();
	}

	@Path("geyser start")
	@Permission(Group.STAFF)
	void startGeyser() {
		send(PREFIX + "Starting geyser animation");
		Pugmas25Geyser.animate();
	}

	@Path("geyser stop")
	@Permission(Group.STAFF)
	void stopGeyser() {
		send(PREFIX + "Stopping geyser animation");
		Pugmas25Geyser.reset();
	}

	@Path("intro start")
	@Permission(Group.ADMIN)
	void intro_start() {
		Pugmas25Intro.play(player());
	}

	@Path("train start")
	@Permission(Group.ADMIN)
	void train_start() {
		Pugmas25Train.start();
	}

	@Path("train crossings open")
	@Permission(Group.ADMIN)
	void train_crossings_open() {
		Pugmas25Train.trainCrossings.openCrossings();
	}

	@Path("train crossings close")
	@Permission(Group.ADMIN)
	void train_crossings_close() {
		Pugmas25Train.trainCrossings.closeCrossings();
	}

	@Permission(Group.STAFF)
	@Path("balloon menu")
	void balloon_menu() {
		if (Pugmas25BalloonEditor.isBeingUsed()) {
			if (!Pugmas25BalloonEditorUtils.isEditing(player()))
				error(Pugmas25BalloonEditorUtils.getEditorName() + " is currently using this");
		} else {
			if (Nexus.isMaintenanceQueued())
				error("Server maintenance is queued, try again later");

			if (!PlayerUtils.hasRoomFor(player(), Pugmas25BlockReplaceBrushMenu.getBrushItem().build()))
				error("Not enough room in your inventory to do this");

			Pugmas25BalloonEditor.editBalloon(nerd());
		}

		if (Pugmas25BalloonEditor.isSavingSchem())
			throw new InvalidInputException("Please wait while your balloon is saving");

		new Pugmas25BalloonEditorMenu().open(player());
	}

	@Path("balloon endSession")
	@Description("End the session without saving")
	@Permission(Group.ADMIN)
	void balloon_end() {
		if (!Pugmas25BalloonEditor.isBeingUsed())
			error("The editor is not being used");

		send(PREFIX + "Ended " + Pugmas25BalloonEditorUtils.getEditorName() + "'s session without saving");
		Pugmas25BalloonEditor.reset();
	}

	@Path("balloon listPlacements")
	@Description("List the currently placed balloons and their regions")
	@Permission(Group.ADMIN)
	void balloon_listPlacements() {
		send("Placed balloons:");
		for (String regionId : Pugmas25BalloonManager.getUserPlacementRegions().keySet()) {
			Nerd nerd = Nerd.of(Pugmas25BalloonManager.getUserPlacementRegions().get(regionId));
			String uuid = nerd.getUniqueId().toString();
			String nickname = nerd.getNickname();

			send(json(" - " + regionId + " = ").group().next(nickname).hover(uuid).insert(uuid));
		}
	}
}
