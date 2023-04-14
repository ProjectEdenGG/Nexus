package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.features.listeners.Podiums.Podium;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import lombok.NonNull;

@Permission(Group.SENIOR_STAFF)
public class PodiumsCommand extends CustomCommand {

	public PodiumsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@Description("Update a podium")
	void update(Podium podium) {
		podium.updateActual();
		send(PREFIX + "Updated");
	}

}
