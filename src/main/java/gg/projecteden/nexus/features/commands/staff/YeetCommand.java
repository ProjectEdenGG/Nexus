package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;

@Permission(Group.STAFF)
public class YeetCommand extends CustomCommand {

	public YeetCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void run(Nerd nerd) {
		if (nerd.getWorldGroup().isMinigames())
			error("Cannot slap " + nerd.getNickname() + ", they are in minigames");

		int wait = 0;
		for (int i = 0; i < 100; i++)
			Tasks.wait(wait += 3, () -> runCommand("slap " + nerd.getName()));
	}

}
