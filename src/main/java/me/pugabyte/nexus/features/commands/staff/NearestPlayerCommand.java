package me.pugabyte.nexus.features.commands.staff;

import eden.utils.Utils.MinMaxResult;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.entity.Player;

@Permission("group.staff")
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
