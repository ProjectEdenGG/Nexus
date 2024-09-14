package gg.projecteden.nexus.features.parkour;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.FormatType;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.TimespanBuilder;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.parkour.LobbyParkourCourse;
import gg.projecteden.nexus.models.parkour.LobbyParkourCourseService;
import gg.projecteden.nexus.models.parkour.LobbyParkourUser.CourseData;
import gg.projecteden.nexus.models.parkour.LobbyParkourUserService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.NonNull;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;

public class ParkourCommand extends CustomCommand {
	private final LobbyParkourCourseService courseService = new LobbyParkourCourseService();
	private final LobbyParkourUserService userService = new LobbyParkourUserService();

	public ParkourCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		new ParkourListener();
	}

	@Path("quit")
	@Description("Quit your current lobby parkour course and teleport out")
	void quit() {
		userService.edit(player(), user -> user.getCourses().forEach(CourseData::quit));
		send(PREFIX + "Quit parkour");
		if (world().getName().equals("gameworld"))
			runCommand("sw newgl");
		else if (world().getName().equals("server"))
			runCommand("hub warp tp nearest");
	}

	@Path("reset")
	@Description("Teleport to the start of your current lobby parkour course")
	void reset() {
		final Set<ProtectedRegion> regions = new WorldGuardUtils(player()).getRegionsLikeAt("lobby_parkour_.*", location());

		if (regions.isEmpty())
			error("You are not in a parkour");

		final String[] split = regions.iterator().next().getId().split("_", 4);
		final LobbyParkourCourse course = new LobbyParkourCourseService().get(UUID.nameUUIDFromBytes(split[2].getBytes()));
		if (course.getCheckpoints().isEmpty())
			error("Course has no configured checkpoints");

		new LobbyParkourUserService().edit(player(), user -> {
			user.get(course).setLeftStartRegion(false);
			// No /back
			player().teleportAsync(course.getCheckpoints().get(0));
			user.sendMessage(PREFIX + "Teleported to start");
		});
	}

	@Path("create <course>")
	@Permission(Group.ADMIN)
	@Description("Create a new lobby parkour course")
	void create(String course) {
		courseService.save(new LobbyParkourCourse(UUID.nameUUIDFromBytes(course.getBytes()), course));
		send(PREFIX + "Course created");
	}

	@Path("delete <course>")
	@Description("Delete a lobby parkour course")
	@Permission(Group.ADMIN)
	void delete(LobbyParkourCourse course) {
		courseService.delete(course);
		send(PREFIX + "Course deleted");
	}

	@Path("checkpoints add <course>")
	@Permission(Group.ADMIN)
	@Description("Add pitch & yaw sensitive checkpoints that players will be teleported back to when they fall (incl. start)")
	void checkpoints_add(LobbyParkourCourse course) {
		course.getCheckpoints().add(location());
		courseService.save(course);
		send(PREFIX + "Checkpoint added");
	}

	@Path("checkpoints clear <course>")
	@Permission(Group.ADMIN)
	@Description("Delete all checkpoints for a parkour course")
	void checkpoints_clear(LobbyParkourCourse course) {
		course.getCheckpoints().clear();
		courseService.save(course);
		send(PREFIX + "Checkpoints cleared");
	}

	@Path("checkpoints tp <course> <checkpoint>")
	@Permission(Group.ADMIN)
	@Description("Teleport to a parkour course's checkpoints")
	void checkpoints_tp(LobbyParkourCourse course, int checkpoint) {
		player().teleportAsync(course.getCheckpoints().get(checkpoint), TeleportCause.COMMAND);
	}

	@Async
	@Path("leaderboard <course> [page]")
	@Description("View course leaderboard")
	void leaderboard(LobbyParkourCourse course, @Arg("1") int page) {
		final List<CourseData> data = userService.getAll().stream()
			.map(user -> user.get(course))
			.filter(courseData -> courseData.getBestRunTime() > 0)
			.sorted(Comparator.comparing(CourseData::getBestRunTime))
			.toList();

		if (data.isEmpty())
			error("No parkour scores logged");

		send(PREFIX + "Fastest times for &e" + course.getName() + " &3course");

		final BiFunction<CourseData, String, JsonBuilder> formatter = (run, index) ->
			json("&3" + index + " &e" + run.getNickname() + " &7- " + TimespanBuilder.ofMillis(run.getBestRunTime()).displayMillis().build().format(FormatType.SHORT));

		paginate(data, formatter, "/parkour leaderboard " + course.getName(), page);
	}

	@Path("hologram update <course>")
	@Permission(Group.ADMIN)
	@Description("Update the parkour leaderboard hologram with the latest data")
	void hologram_update(LobbyParkourCourse course) {
		course.updateHologram();
	}

	@TabCompleterFor(LobbyParkourCourse.class)
	List<String> tabCompleteLobbyParkourCourse(String filter) {
		return courseService.getAll().stream()
			.map(LobbyParkourCourse::getName)
			.filter(name -> name.toLowerCase().startsWith(filter.toLowerCase()))
			.toList();
	}

	@ConverterFor(LobbyParkourCourse.class)
	LobbyParkourCourse convertToLobbyParkourCourse(String value) {
		return courseService.get(UUID.nameUUIDFromBytes(value.getBytes()));
	}

}
