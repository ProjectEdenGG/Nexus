package gg.projecteden.nexus.features.hub;

import gg.projecteden.annotations.Async;
import gg.projecteden.nexus.features.warps.commands._WarpCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.hub.HubParkourCourse;
import gg.projecteden.nexus.models.hub.HubParkourCourseService;
import gg.projecteden.nexus.models.hub.HubParkourUser.CourseData;
import gg.projecteden.nexus.models.hub.HubParkourUserService;
import gg.projecteden.nexus.models.hub.HubTreasureHunter;
import gg.projecteden.nexus.models.hub.HubTreasureHunterService;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.models.warps.Warps.Warp;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.utils.TimeUtils.Timespan;
import gg.projecteden.utils.TimeUtils.Timespan.FormatType;
import lombok.NonNull;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

import static gg.projecteden.nexus.features.hub.HubTreasureHunt.TOTAL_TREASURE_CHESTS;

@Redirect(from = {"/tphub", "/lobby"}, to = "/hub")
public class HubCommand extends _WarpCommand {
	final HubParkourCourseService courseService = new HubParkourCourseService();
	final HubParkourUserService userService = new HubParkourUserService();

	public HubCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.HUB;
	}

	@Path
	void warp() {
		if (isStaff())
			WarpType.STAFF.get("hub").teleportAsync(player());
		else
			WarpType.NORMAL.get("hub").teleportAsync(player());
	}

	@Override
	@Path("warps [filter]")
	public void list(@Arg(tabCompleter = Warp.class) String filter) {
		super.list(filter);
	}

	@Path("parkour quit")
	void parkour_quit() {
		userService.edit(player(), user -> user.getCourses().forEach(CourseData::quit));
		send(PREFIX + "Quit parkour");
		teleportNearest();
	}

	@Path("parkour create <course>")
	@Permission(Group.ADMIN)
	void parkour_create(String course) {
		courseService.save(new HubParkourCourse(UUID.nameUUIDFromBytes(course.getBytes()), course));
		send(PREFIX + "Parkour course created");
	}

	@Path("parkour delete <course>")
	@Permission(Group.ADMIN)
	void parkour_delete(HubParkourCourse course) {
		courseService.delete(course);
		send(PREFIX + "Parkour course deleted");
	}

	@Path("parkour checkpoints add <course>")
	@Permission(Group.ADMIN)
	@Description("Add checkpoints that players will be teleported back to when they fall (incl. start)")
	void parkour_checkpoints_add(HubParkourCourse course) {
		course.getCheckpoints().add(location());
		courseService.save(course);
		send(PREFIX + "Parkour checkpoint added");
	}

	@Path("parkour checkpoints clear <course>")
	@Permission(Group.ADMIN)
	@Description("Reset all checkpoints for a parkour course")
	void parkour_checkpoints_clear(HubParkourCourse course) {
		course.getCheckpoints().clear();
		courseService.save(course);
		send(PREFIX + "Parkour checkpoints cleared");
	}

	@Path("parkour checkpoints tp <course> <checkpoint>")
	@Permission(Group.ADMIN)
	@Description("Teleport to a parkour course's checkpoints")
	void parkour_checkpoints_tp(HubParkourCourse course, int checkpoint) {
		player().teleportAsync(course.getCheckpoints().get(checkpoint));
	}

	@Async
	@Path("parkour top <course> [page]")
	@Description("View course leaderboard")
	void parkour_top(HubParkourCourse course, @Arg("1") int page) {
		final List<CourseData> data = userService.getAll().stream()
			.map(user -> user.get(course))
			.filter(courseData -> courseData.getBestRunTime() > 0)
			.sorted(Comparator.comparing(CourseData::getBestRunTime))
			.toList();

		if (data.isEmpty())
			error("No parkour scores logged");

		send(PREFIX + "Fastest times for &e" + course.getName() + " &3course");

		final BiFunction<CourseData, String, JsonBuilder> formatter = (run, index) ->
			json("&3" + index + " &e" + run.getNickname() + " &7- " + Timespan.ofMillis(run.getBestRunTime()).format(FormatType.SHORT));

		paginate(data, formatter, "/hub parkour top " + course.getName(), page);
	}

	@Path("parkour hologram update [course]")
	@Permission(Group.ADMIN)
	void parkour_hologram_update(HubParkourCourse course) {
		course.updateHologram();
	}

	@Path("treasurehunt")
	void treasurehunt() {
		final HubTreasureHunterService service = new HubTreasureHunterService();
		final HubTreasureHunter hunter = service.get(player());
		final int found = hunter.getFound().size();
		if (found != TOTAL_TREASURE_CHESTS)
			send(PREFIX + "You found %s of %s treasure chests".formatted(found, TOTAL_TREASURE_CHESTS));
		else
			send(PREFIX + "You found all the treasure chests");
	}

	@TabCompleterFor(HubParkourCourse.class)
	List<String> tabCompleteHubParkourCourse(String filter) {
		return courseService.getAll().stream()
			.map(HubParkourCourse::getName)
			.filter(name -> name.toLowerCase().startsWith(filter.toLowerCase()))
			.toList();
	}

	@ConverterFor(HubParkourCourse.class)
	HubParkourCourse convertToHubParkourCourse(String value) {
		return courseService.get(UUID.nameUUIDFromBytes(value.getBytes()));
	}

}
