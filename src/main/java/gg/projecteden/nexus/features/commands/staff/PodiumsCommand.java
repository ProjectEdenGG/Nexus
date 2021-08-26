package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.features.listeners.Podiums.Podium;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

@Permission("group.seniorstaff")
public class PodiumsCommand extends CustomCommand {

	public PodiumsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("update <leaderboard>")
	void update(Podium podium) {
		podium.updateActual();
		send(PREFIX + "Updated");
	}

}