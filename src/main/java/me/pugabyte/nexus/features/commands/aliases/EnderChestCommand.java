package me.pugabyte.nexus.features.commands.aliases;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.OfflinePlayer;

@Aliases("ec")
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
