package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.api.common.utils.Utils.MinMaxResult;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.entity.Player;

@Permission(Group.STAFF)
public class NearestPlayerCommand extends CustomCommand {

	public NearestPlayerCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void nearestPlayer() {
		MinMaxResult<Player> result = PlayerUtils.getNearestPlayer(player());

		if (result.getObject() != null)
			send(PREFIX + result.getObject().getName() + " is " + result.getValue().intValue() + " blocks away");
		else
			error("No players are nearby");
	}
}
