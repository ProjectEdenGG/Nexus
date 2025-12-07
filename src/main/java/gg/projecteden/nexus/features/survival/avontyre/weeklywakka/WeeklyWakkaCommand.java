package gg.projecteden.nexus.features.survival.avontyre.weeklywakka;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.warps.commands._WarpCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.models.warps.Warps.Warp;
import gg.projecteden.nexus.models.weeklywakka.WeeklyWakka;
import gg.projecteden.nexus.models.weeklywakka.WeeklyWakkaService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Aliases("ww")
public class WeeklyWakkaCommand extends _WarpCommand {

	public WeeklyWakkaCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Override
	public String getPermission() {
		return Group.ADMIN;
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.WEEKLY_WAKKA;
	}

	@Path("info")
	@Description("Get information about the Weekly Wakka challenge")
	void info() {
		WeeklyWakkaUtils.tell(player(), "&fHey there! I have hidden a clone of myself somewhere in Avontyre. ");

		Tasks.wait(TickTime.SECOND.x(2), () -> {
			WeeklyWakkaUtils.tell(player(), "If you find him, you'll get a key to open my crate here. "
					+ "He will move locations once a week, so you're able to get a new reward every week!");

			if (WeeklyWakkaUtils.hasTrackingDevice(player()))
				return;

			Tasks.wait(TickTime.SECOND.x(4), () ->
					WeeklyWakkaUtils.tell(player(), new JsonBuilder()
							.next("&fIf you need help finding him, I can give you a detector to help find his general location. Want it? ").group()
							.next("&e&l[&eTake Detector&e&l]").command("/weeklywakka getDetector").hover("&eClick to take the detector!")
					)
			);
		});
	}

	@Path("check")
	@Description("Check if you have found this week's Weekly Wakka")
	void check() {
		WeeklyWakkaService service = new WeeklyWakkaService();
		WeeklyWakka weeklyWakka = service.get0();

		if (!weeklyWakka.getFoundPlayers().contains(uuid())) {
			send(PREFIX + "You have &cnot &3found the wakka this week! He resets in &e" + WeeklyWakkaFeature.getNextWeek() + "&3, hurry up!");
			return;
		}

		send(PREFIX + "You have found wakka this week! He resets in &e" + WeeklyWakkaFeature.getNextWeek());
	}

	@HideFromHelp
	@HideFromWiki
	@Path("getDetector")
	void getDetector() {
		if (WeeklyWakkaUtils.hasTrackingDevice(player()))
			error("You already have a Wakka Detector!");

		PlayerUtils.giveItem(player(), WeeklyWakkaUtils.getDetector());
		new SoundBuilder(Sound.ENTITY_ITEM_PICKUP).receiver(player()).location(player()).volume(0.5).play();

	}

	@Path("moveNPC")
	@Permission(Group.ADMIN)
	@Description("Move the Weekly Wakka NPC to a new location")
	void moveNPC() {
		WeeklyWakkaFeature.moveNPC(player());
	}

	@Path("tp")
	@Permission(Group.ADMIN)
	@Description("Teleport to the Weekly Wakka NPC's location")
	void tp() {
		send(PREFIX + "Teleporting to location #" + new WeeklyWakkaService().get().getCurrentLocation());
		player().teleportAsync(WeeklyWakkaUtils.getNPC().getStoredLocation(), TeleportCause.COMMAND);
	}

	@Path("found")
	@Permission(Group.ADMIN)
	@Description("Get a list of players who have found the Weekly Wakka this week")
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
	@Description("Remove a player from the list of players who have found the Weekly Wakka this week")
	void unfind(@Arg("self") Player player) {
		WeeklyWakkaService service = new WeeklyWakkaService();
		WeeklyWakka ww = service.get();
		ww.getFoundPlayers().remove(player.getUniqueId());
		service.save(ww);

		send(PREFIX + "Removed " + nickname(player) + " from the found list");
	}

	@Path("getTip <index>")
	@Permission(Group.ADMIN)
	@Description("Get a tip from the Weekly Wakka tips")
	void getTip(int index) {
		WeeklyWakkaUtils.tell(player(), WeeklyWakkaUtils.getTips().get(index).get());
	}

	@Path("(list|warps) [filter]")
	@Description("List available warps")
	@Permission(Group.ADMIN)
	public void list(@Arg(tabCompleter = Warp.class) String filter) {
		super.list(filter);
	}

	@Path("(set|create) <name>")
	@Permission(Group.ADMIN)
	@Description("Create a new warp")
	public void set(@Arg(tabCompleter = Warp.class) String name) {
		super.set(name);
	}

	@Path("reset <name>")
	@Description("Update a warp's location")
	@Permission(Group.ADMIN)
	public void reset(@Arg(tabCompleter = Warp.class) String name) {
		super.reset(name);
	}

	@Override
	@Path("(teleport|tp|warp) <name>")
	@Permission(Group.ADMIN)
	@Description("Teleport to a Weekly Wakka warp")
	public void teleport(Warp warp) {
		super.teleport(warp);
	}

	@Override
	@Path("<name>")
	@Permission(Group.ADMIN)
	@Description("Teleport to a Weekly Wakka warp")
	public void tp(Warp warp) {
		super.tp(warp);
	}

	@Path("tp nearest")
	@Override
	@Permission(Group.ADMIN)
	@Description("Teleport to the nearest Weekly Wakka warp")
	public void teleportNearest() {
		super.teleportNearest();
	}

	@Path("nearest")
	@Override
	@Permission(Group.ADMIN)
	@Description("View the nearest Weekly Wakka warp")
	public void nearest() {
		super.nearest();
	}

	@Path("farthest [page]")
	@Permission(Group.ADMIN)
	void farthest(@Arg("1") Integer page) {
		var selection = worldedit().getPlayerSelection(player());
		var locations = getWarpType().getAll().stream().map(Warp::getLocation).toList();

		if (locations.isEmpty()) {
			send("&cNo warps found to use as POIs");
			return;
		}

		int minX = selection.getMinimumPoint().getBlockX();
		int maxX = selection.getMaximumPoint().getBlockX();
		int minZ = selection.getMinimumPoint().getBlockZ();
		int maxZ = selection.getMaximumPoint().getBlockZ();
		int y = (int) (locations.stream().mapToDouble(Location::getY).sum() / locations.size());
		send(PREFIX + "&3Average Y value: &e" + y);

		List<GridSection> sections = new ArrayList<>();

		for (int x = minX; x < maxX; x += 10) {
			for (int z = minZ; z < maxZ; z += 10) {
				int centerX = x + 5;
				int centerZ = z + 5;
				Location sectionCenter = new Location(player().getWorld(), centerX, y, centerZ);

				if (!selection.contains(worldedit().toBlockVector3(sectionCenter)))
					continue;

				double nearestDistance = locations.stream()
					.mapToDouble(sectionCenter::distanceSquared)
					.min()
					.orElse(0);

				sections.add(new GridSection(x, z, sectionCenter, nearestDistance));
			}
		}

		sections.sort((a, b) -> Double.compare(b.nearestDistance, a.nearestDistance));

		send(PREFIX + "Most barren areas:");
		new Paginator<GridSection>()
			.values(sections)
			.formatter((section, index) ->
				json("&3%s &e(%d, %d) &7- %s blocks".formatted(
					index,
					section.x,
					section.z,
					String.format("%.1f", Math.sqrt(section.nearestDistance))
				)).command(StringUtils.tppos(section.center))
			)
			.command("/weeklywakka farthest")
			.page(page)
			.send();
	}

	private static class GridSection {
		int x;
		int z;
		Location center;
		double nearestDistance;

		GridSection(int x, int z, Location center, double nearestDistance) {
			this.x = x;
			this.z = z;
			this.center = center;
			this.nearestDistance = nearestDistance;
		}
	}

}
