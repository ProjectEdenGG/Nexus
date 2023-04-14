package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Permission(Group.STAFF)
@Aliases({"checkperm", "permtest", "testperm", "hasperm"})
public class PermCheckCommand extends CustomCommand {

	public PermCheckCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Check if you have a permission")
	void check(String permission) {
		check(player(), permission);
	}

	@NoLiterals
	@Description("Check if a player has a permission")
	void check(Player player, String permission) {
		if (player.hasPermission(permission))
			send("&a✔ " + player.getName() + " has permission " + permission);
		else
			send("&c✗ " + player.getName() + " does not have permission " + permission);
	}

}
