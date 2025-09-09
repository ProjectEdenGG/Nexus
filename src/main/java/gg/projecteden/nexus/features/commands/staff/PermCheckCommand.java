package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Permission(Group.STAFF)
@Aliases({"checkperm", "permtest", "testperm", "hasperm"})
public class PermCheckCommand extends CustomCommand {

	public PermCheckCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<permission>")
	@Description("Check if you have a permission")
	void check(String permission) {
		check(player(), permission);
	}

	@Path("<player> <permission>")
	@Description("Check if a player has a permission")
	void check(Player player, String permission) {
		if (player.hasPermission(permission))
			send("&a✔ " + player.getName() + " has permission " + permission);
		else
			send("&c✗ " + player.getName() + " does not have permission " + permission);
	}

}
