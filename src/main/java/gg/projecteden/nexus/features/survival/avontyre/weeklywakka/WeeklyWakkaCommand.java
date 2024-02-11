package gg.projecteden.nexus.features.survival.avontyre.weeklywakka;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.warps.commands._WarpCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.models.weeklywakka.WeeklyWakka;
import gg.projecteden.nexus.models.weeklywakka.WeeklyWakkaService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Aliases("ww")
public class WeeklyWakkaCommand extends _WarpCommand {

	public WeeklyWakkaCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.WEEKLY_WAKKA;
	}

	@Path("info")
	void info() {
		WeeklyWakkaUtils.tell(player(), "&fHey there! I have hidden a clone of myself somewhere in Avontyre. ");

		Tasks.wait(TickTime.SECOND.x(2), () -> {
			WeeklyWakkaUtils.tell(player(), "If you find him, you'll get a key to open my crate here. "
				+ "He will move locations once a week, so you're able to get a new reward every week!");

			if (!WeeklyWakkaUtils.hasTrackingDevice(player())) {
				Tasks.wait(TickTime.SECOND.x(4), () -> {
					WeeklyWakkaUtils.tell(player(), new JsonBuilder()
						.next("&fIf you need help finding him, I can give you a detector to help find his general location. Want it? ").group()
						.next("&e&l[&eTake Detector&e&l]").command("/weeklywakka getDetector").hover("&eClick to take the detector!"));
				});
			}
		});
	}

	@Path("getDetector")
	void getDetector() {
		PlayerUtils.giveItem(player(), WeeklyWakkaUtils.getDetector());
		new SoundBuilder(Sound.ENTITY_ITEM_PICKUP).receiver(player()).location(player()).volume(0.5).play();

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

	@Path("unfind [player]")
	@Permission(Group.ADMIN)
	void unfind(@Arg("self") Player player) {
		WeeklyWakkaService service = new WeeklyWakkaService();
		WeeklyWakka ww = service.get();
		ww.getFoundPlayers().remove(player.getUniqueId());
		service.save(ww);

		send(PREFIX + "Removed " + nickname(player) + " from the found list");
	}

	@Path("getTip <index>")
	@Permission(Group.ADMIN)
	void getTip(int index) {
		WeeklyWakkaUtils.tell(player(), WeeklyWakkaUtils.getTips().get(index).get());
	}

}
