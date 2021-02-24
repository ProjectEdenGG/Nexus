package me.pugabyte.nexus.features.commands.aliases;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.OfflinePlayer;

@Permission("group.admin")
public class LWCCOCommand extends CustomCommand {

	public LWCCOCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void run(OfflinePlayer player) {
		runCommand("lwc admin forceowner " + player.getName());
	}
}
