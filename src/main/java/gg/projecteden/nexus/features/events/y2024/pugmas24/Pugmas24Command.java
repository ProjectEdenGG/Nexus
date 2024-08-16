package gg.projecteden.nexus.features.events.y2024.pugmas24;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.IEventCommand;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24.Pugmas24DeathCause;
import gg.projecteden.nexus.features.events.y2024.pugmas24.advent.Pugmas24Advent;
import gg.projecteden.nexus.features.events.y2024.pugmas24.advent.Pugmas24AdventMenu;
import gg.projecteden.nexus.features.events.y2024.pugmas24.balloons.Pugmas24BalloonEditor;
import gg.projecteden.nexus.features.events.y2024.pugmas24.balloons.Pugmas24BalloonEditorMenu;
import gg.projecteden.nexus.features.events.y2024.pugmas24.balloons.Pugmas24BalloonEditorUtils;
import gg.projecteden.nexus.features.events.y2024.pugmas24.balloons.Pugmas24BalloonManager;
import gg.projecteden.nexus.features.events.y2024.pugmas24.balloons.Pugmas24BlockReplaceBrushMenu;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Pugmas24Districts;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Pugmas24Districts.Pugmas24District;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Pugmas24Geyser;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Pugmas24SlotMachine;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Pugmas24SlotMachine.Pugmas24SlotMachineReward;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Pugmas24SlotMachine.Pugmas24SlotMachineReward.Pugmas24SlotMachineRewardType;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Pugmas24SlotMachine.Pugmas24SlotMachineRewardMenu;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Pugmas24Train;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.pugmas24.Advent24Config;
import gg.projecteden.nexus.models.pugmas24.Advent24ConfigService;
import gg.projecteden.nexus.models.pugmas24.Advent24Present;
import gg.projecteden.nexus.models.pugmas24.Pugmas24User;
import gg.projecteden.nexus.models.pugmas24.Pugmas24UserService;
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

@NoArgsConstructor
@Aliases("pugmas")
@Redirect(from = "/advent", to = "/pugmas24 advent")
public class Pugmas24Command extends IEventCommand implements Listener {
	public String PREFIX = Pugmas24.PREFIX;

	private final Pugmas24UserService service = new Pugmas24UserService();
	private Pugmas24User user;

	private final Advent24ConfigService adventService = new Advent24ConfigService();
	private final Advent24Config adventConfig = adventService.get0();

	public Pugmas24Command(@NonNull CommandEvent event) {
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
		return Pugmas24.get();
	}

	@Path
	void pugmas() {
		if (!user.isVisited())
			error("You need to take the Pugmas train at Hub to unlock this warp.");

		player().teleportAsync(Pugmas24.get().warp, TeleportCause.COMMAND);
	}

	@Path("health reset")
	@Permission(Group.ADMIN)
	void health_reset() {
		Pugmas24.get().resetHealth(player());
	}

	@Path("health add")
	@Permission(Group.ADMIN)
	void health_add() {
		Pugmas24.get().addMaxHealth(player(), 2.0);
	}

	@Path("health subtract")
	@Permission(Group.ADMIN)
	void health_subtract() {
		Pugmas24.get().subtractMaxHealth(player(), 2.0);
	}

	@Path("health set <amount>")
	@Permission(Group.ADMIN)
	void health_set(@Arg(value = "20", min = 1, max = 40) int amount) {
		Pugmas24.get().setMaxHealth(player(), amount);
	}

	@Path("district")
	@Description("View which district you are currently in")
	@Permission(Group.ADMIN)
	void district() {
		Pugmas24District district = Pugmas24Districts.of(player());
		send(PREFIX + "Area Designation: &e" + district.getName());
	}

	@Path("advent")
	@Description("Open the advent calender")
	void advent(
			@Arg(value = "0", permission = Group.ADMIN) @Switch int day,
			@Arg(value = "30", permission = Group.ADMIN) @Switch int frameTicks
	) {

		LocalDate date = LocalDate.now();
		if (date.isBefore(Pugmas24.get().getStart()) || day > 0)
			date = Pugmas24.get().getStart().plusDays(day - 1);

		new Pugmas24AdventMenu(user, date, frameTicks).open(player());
	}

	@Path("advent waypoint <day>")
	@Description("Get directions to a present you've already found")
	void advent_waypoint(int day) {
		if (!user.advent().hasFound(day))
			error("You have not found day &e#" + day);

		Pugmas24Advent.glow(user, day);
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
		Advent24Present nearestPresent = Collections.min(adventConfig.getPresents(), Comparator.comparing(present -> distanceTo(present).get()));

		if (nearestPresent == null)
			error("None found");

		send(PREFIX + "Nearest: #" + nearestPresent.getDay());
	}

	@Path("advent get <day>")
	@Permission(Group.ADMIN)
	void advent_get(@Arg(min = 1, max = 25) int day) {
		giveItem(Advent24Config.get().get(day).getItem().build());
	}

	@Path("advent config updateItems")
	@Permission(Group.ADMIN)
	void advent_updateItems() {
		Pugmas24Advent.updateItems();

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

	@Path("death <cause>")
	@Permission(Group.ADMIN)
	void death_test(Pugmas24DeathCause deathCause) {
		Pugmas24.get().onDeath(player(), deathCause);
	}

	@Path("slotMachine rewards")
	void slotMachine_rewards() {
		new Pugmas24SlotMachineRewardMenu().open(player());
	}

	@Path("slotMachine roll")
	@Permission(Group.ADMIN)
	void slotMachine() {
		Pugmas24SlotMachine.roll(player());
	}

	@Path("slotMachine simulateWin <reward> <type>")
	@Permission(Group.ADMIN)
	void slotMachine_reward(Pugmas24SlotMachineReward reward, Pugmas24SlotMachineRewardType type) {
		reward.give(player(), type);
	}

	@Path("slotMachine setup")
	@Permission(Group.ADMIN)
	void slotMachine_setup() {
		Pugmas24SlotMachine.init(player());
	}

	@Path("slotMachine reset")
	@Permission(Group.ADMIN)
	void slotMachine_reset() {
		Pugmas24SlotMachine.reset();
	}

	@Path("geyser start")
	@Permission(Group.STAFF)
	void startGeyser() {
		send(PREFIX + "Starting geyser animation");
		Pugmas24Geyser.animate();
	}

	@Path("geyser stop")
	@Permission(Group.STAFF)
	void stopGeyser() {
		send(PREFIX + "Stopping geyser animation");
		Pugmas24Geyser.reset();
	}

	@Path("train start")
	@Permission(Group.ADMIN)
	void train_start() {
		Pugmas24Train.start();
	}

	@Permission(Group.STAFF)
	@Path("balloon menu")
	void balloon_menu() {
		if (Pugmas24BalloonEditor.isBeingUsed()) {
			if (!Pugmas24BalloonEditorUtils.isEditing(player()))
				error(Pugmas24BalloonEditorUtils.getEditorName() + " is currently using this");
		} else {
			if (Nexus.isMaintenanceQueued())
				error("Server maintenance is queued, try again later");

			if (!PlayerUtils.hasRoomFor(player(), Pugmas24BlockReplaceBrushMenu.getBrushItem().build()))
				error("Not enough room in your inventory to do this");

			Pugmas24BalloonEditor.editBalloon(nerd());
		}

		if (Pugmas24BalloonEditor.isSavingSchem())
			throw new InvalidInputException("Please wait while your balloon is saving");

		new Pugmas24BalloonEditorMenu().open(player());
	}

	@Path("balloon endSession")
	@Description("End the session without saving")
	@Permission(Group.ADMIN)
	void balloon_end() {
		if (!Pugmas24BalloonEditor.isBeingUsed())
			error("The editor is not being used");

		send(PREFIX + "Ended " + Pugmas24BalloonEditorUtils.getEditorName() + "'s session without saving");
		Pugmas24BalloonEditor.reset();
	}

	@Path("balloon listPlacements")
	@Description("List the currently placed balloons and their regions")
	@Permission(Group.ADMIN)
	void balloon_listPlacements() {
		send("Placed balloons:");
		for (String regionId : Pugmas24BalloonManager.getUserPlacementRegions().keySet()) {
			Nerd nerd = Nerd.of(Pugmas24BalloonManager.getUserPlacementRegions().get(regionId));
			String uuid = nerd.getUniqueId().toString();
			String nickname = nerd.getNickname();

			send(json(" - " + regionId + " = ").group().next(nickname).hover(uuid).insert(uuid));
		}
	}
}
