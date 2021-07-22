package gg.projecteden.nexus.features.commands.aliases;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.OfflinePlayer;

@Aliases({"ec", "echest"})
@Permission("group.staff")
public class EnderChestCommand extends CustomCommand {

	public EnderChestCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void run(@Arg(value = "self", permission = "group.moderator") OfflinePlayer player) {
		runCommand("openender " + player.getName());
	}
}
