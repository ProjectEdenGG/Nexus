package gg.projecteden.nexus.features.commands.aliases;

import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import org.bukkit.OfflinePlayer;

@Aliases({"ec", "echest"})
@Permission(Group.STAFF)
public class EnderChestCommand extends CustomCommand {

	public EnderChestCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("View your or another player's enderchest")
	void run(@Permission(Group.MODERATOR) @Optional("self") OfflinePlayer player) {
		runCommand("openender " + player.getName());
	}
}
