package gg.projecteden.nexus.features.tournaments;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;

@Permission(Group.ADMIN)
public class ChallongeCommand extends CustomCommand {

	@Path("create")
	void create() {

	}

	@Path("start")
	void start() {

	}

	@Path("list")
	void list() {

	}
}
