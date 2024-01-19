package gg.projecteden.nexus.features.survival.avontyre.weeklywakka;

import gg.projecteden.nexus.features.warps.commands._WarpCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.models.weeklywakka.WeeklyWakkaService;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Permission(Group.STAFF)
public class WeeklyWakkaCommand extends _WarpCommand {

	public WeeklyWakkaCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.WEEKLY_WAKKA;
	}

	@Path("getDevice")
	void getDevice() {
		PlayerUtils.giveItem(player(), WeeklyWakkaUtils.getTrackingDevice());
	}

	@Path("info")
	void info() {
		WeeklyWakkaUtils.tell(player(), "&3Hey there, my name is &eWakka&3, and I'm an admin on Project Eden. "
			+ "&eI have hidden a clone of myself somewhere in Avontyre. "
			+ "&3If you find him, you'll get a key to open my crate here. "
			+ "&3He will move locations once a week, so you're able to get a new reward every week!");
	}

	@Path("moveNPC")
	@Permission(Group.ADMIN)
	void moveNPC() {
		WeeklyWakkaFeature.moveNPC(player());
	}

	@Path("tp")
	@Permission(Group.ADMIN)
	void tp() {
		send(PREFIX + "Teleporting to location #" + new WeeklyWakkaService().get().getCurrentLocation());
		player().teleportAsync(WeeklyWakkaUtils.getNPC().getStoredLocation(), TeleportCause.COMMAND);
	}

	@Path("found")
	@Permission(Group.ADMIN)
	void whoFound() {
		List<UUID> foundPlayers = new WeeklyWakkaService().get().getFoundPlayers();
		if (foundPlayers.isEmpty())
			error("No players have found weekly wakka this week");

		send(PREFIX + "Found: " + foundPlayers.stream()
			.map(Nickname::of)
			.collect(Collectors.joining(", ")));
	}

}
