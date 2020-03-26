package me.pugabyte.bncore.features.freeze;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

@Aliases("ymc")
@Permission("group.staff")
public class YouMayContinueCommand extends CustomCommand {

	public YouMayContinueCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player> [warn...]")
	void player(Player player, String reason) {
		line(2);
		runCommand("unfreeze " + player.getName());
		runCommand("vanish on");
		runCommand("forcechannel " + player.getName() + " g");
		if (!isNullOrEmpty(reason))
			runCommand("warn " + player.getName() + " " + reason);
	}

}
