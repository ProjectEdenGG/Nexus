package gg.projecteden.nexus.features.hub;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
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
import gg.projecteden.nexus.models.hub.HubParkourUserService;
import gg.projecteden.nexus.models.warps.WarpType;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

@Redirect(from = {"/tphub", "/lobby"}, to = "/hub")
public class HubCommand extends CustomCommand {
	final HubParkourCourseService courseService = new HubParkourCourseService();
	final HubParkourUserService userService = new HubParkourUserService();

	public HubCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void warp() {
		WarpType.NORMAL.get("hub").teleportAsync(player());
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
	@Description("Reset all checkpoints for a parkour course")
	void parkour_checkpoints_tp(HubParkourCourse course, int checkpoint) {
		player().teleportAsync(course.getCheckpoints().get(checkpoint));
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
