package gg.projecteden.nexus.features.commands.aliases;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.OfflinePlayer;

@Permission(Group.STAFF)
public class LWCCOCommand extends CustomCommand {

	public LWCCOCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void run(OfflinePlayer player) {
		runCommand("lwc admin forceowner " + player.getName());
	}
}
