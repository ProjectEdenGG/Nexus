package gg.projecteden.nexus.features.events.y2024.pugmas24;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.IEventCommand;
import gg.projecteden.nexus.features.events.y2024.pugmas24.advent.Advent24;
import gg.projecteden.nexus.features.events.y2024.pugmas24.advent.Advent24Menu;
import gg.projecteden.nexus.features.events.y2024.pugmas24.ballooneditor.BalloonEditor;
import gg.projecteden.nexus.features.events.y2024.pugmas24.ballooneditor.BalloonEditorMenu;
import gg.projecteden.nexus.features.events.y2024.pugmas24.ballooneditor.BalloonEditorUtils;
import gg.projecteden.nexus.features.events.y2024.pugmas24.ballooneditor.BlockReplaceBrushMenu;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.District;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Geyser;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Train24;
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
		if (!user.isFirstVisit())
			error("You need to take the Pugmas train at Hub to unlock this warp.");

		player().teleportAsync(Pugmas24.get().warp, TeleportCause.COMMAND);
	}

	@Path("district")
	@Description("View which district you are currently in")
	void district() {
		District district = District.of(location());
		if (district == null)
			error("You must be in Pugmas to run this command");

		send(PREFIX + "You are " + (district == District.UNKNOWN ? "not in a district" : "in the &e" + district.getFullName()));
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

		new Advent24Menu(user, date, frameTicks).open(player());
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
		Advent24.updateItems();

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

	@Path("geyser start")
	@Permission(Group.STAFF)
	void startGeyser() {
		send(PREFIX + "Starting geyser animation");
		Geyser.animate();
	}

	@Path("geyser stop")
	@Permission(Group.STAFF)
	void stopGeyser() {
		send(PREFIX + "Stopping geyser animation");
		Geyser.reset();
	}

	@Path("train start")
	@Permission(Group.ADMIN)
	void train_start() {
		Train24.start();
	}

	@Permission(Group.STAFF)
	@Path("balloon menu")
	void balloon_menu() {
		if (BalloonEditor.isBeingUsed()) {
			if (!BalloonEditorUtils.isEditing(player()))
				error(BalloonEditorUtils.getEditorName() + " is currently using this");
		} else {
			if (Nexus.isMaintenanceQueued())
				error("Server maintenance is queued, try again later");

			if (PlayerUtils.hasRoomFor(player(), BlockReplaceBrushMenu.getBrushItem().build()))
				error("Not enough room in your inventory to do this");

			BalloonEditor.editBalloon(nerd());
		}

		if (BalloonEditor.isSavingSchem())
			throw new InvalidInputException("Please wait while your balloon is saving");

		new BalloonEditorMenu().open(player());
	}

	@Path("balloon endSession")
	@Description("End the session without saving")
	@Permission(Group.ADMIN)
	void balloon_end() {
		if (!BalloonEditor.isBeingUsed())
			error("The editor is not being used");

		send(PREFIX + "Ended " + BalloonEditorUtils.getEditorName() + "'s session without saving");
		BalloonEditor.reset();
	}
}
