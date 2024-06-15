package gg.projecteden.nexus.features.events.y2024.pugmas24;

import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.features.events.y2024.pugmas24.advent.Advent24Menu;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.District24;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.pugmas24.Advent24Config;
import gg.projecteden.nexus.models.pugmas24.Advent24ConfigService;
import gg.projecteden.nexus.models.pugmas24.Advent24Present;
import gg.projecteden.nexus.models.pugmas24.Pugmas24User;
import gg.projecteden.nexus.models.pugmas24.Pugmas24UserService;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;

@NoArgsConstructor
@Aliases("pugmas")
@Redirect(from = "/advent", to = "/pugmas24 advent")
public class Pugmas24Command extends CustomCommand implements Listener {
	public String PREFIX = Pugmas24.PREFIX;
	private final String timeLeft = Timespan.of(Pugmas24.EPOCH).format();

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

	@Path
	void pugmas() {
		if (Pugmas24.isBeforePugmas() && !isStaff())
			error("Soon™ (" + timeLeft + ")");

		if (!user.isFirstVisit())
			error("You need to take the Pugmas train at Spawn to unlock this warp.");

		player().teleportAsync(Pugmas24.warp, TeleportCause.COMMAND);
	}

	@Path("district")
	@Description("View which district you are currently in")
	void district() {
		District24 district = District24.of(location());
		if (district == null)
			error("You must be in Pugmas to run this command");

		send(PREFIX + "You are " + (district == District24.UNKNOWN ? "not in a district" : "in the &e" + district.getFullName()));
	}

	@Path("advent")
	@Description("Open the advent calender")
	void advent(
			@Arg(value = "0", permission = Group.ADMIN) @Switch int day,
			@Arg(value = "30", permission = Group.ADMIN) @Switch int frameTicks
	) {
		verifyDate();

		LocalDate date = Pugmas24.TODAY;
		if (date.isBefore(Pugmas24.EPOCH) || day > 0)
			date = Pugmas24.EPOCH.plusDays(day - 1);

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

		send("Nearest: #" + nearestPresent.getDay());
	}

	@Path("advent get <day>")
	@Permission(Group.ADMIN)
	void advent_get(@Arg(min = 1, max = 25) int day) {
		giveItem(Advent24Config.get().get(day).getItem().build());
	}

	//

	private void verifyDate() {
		if (!isAdmin()) {
			if (Pugmas24.isBeforePugmas())
				error("Soon™ (" + timeLeft + ")");

			if (Pugmas24.isPastPugmas())
				error("Next year!");
		}
	}

}
