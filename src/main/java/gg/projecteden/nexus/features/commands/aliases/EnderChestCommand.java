package gg.projecteden.nexus.features.commands.aliases;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.OfflinePlayer;

@Aliases({"ec", "echest"})
@Permission(Group.STAFF)
@Description("Open your enderchest.")
public class EnderChestCommand extends CustomCommand {

	public EnderChestCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void run(@Arg(value = "self", permission = Group.MODERATOR) OfflinePlayer player) {
		runCommand("openender " + player.getName());
	}
}
