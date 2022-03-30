package gg.projecteden.nexus.features.noteblocks;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.noteblock.NoteBlockTracker;
import gg.projecteden.nexus.models.noteblock.NoteBlockTrackerService;

@Permission(Group.ADMIN)
public class NoteBlockCommand extends CustomCommand {
	private static final NoteBlockTrackerService trackerService = new NoteBlockTrackerService();
	private static NoteBlockTracker tracker;

	public NoteBlockCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			tracker = trackerService.get(location());
	}

	@Path("info")
	void info() {
		send("todo");
	}
}
