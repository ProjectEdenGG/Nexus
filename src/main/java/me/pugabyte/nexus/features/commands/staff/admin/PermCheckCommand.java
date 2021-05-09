package me.pugabyte.nexus.features.commands.staff.admin;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

@Permission("group.staff")
@Aliases({"checkperm", "permtest", "testperm", "hasperm"})
public class PermCheckCommand extends CustomCommand {

	public PermCheckCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<permission>")
	void check(String permission) {
		check(player(), permission);
	}

	@Path("<player> <permission>")
	void check(Player player, String permission) {
		if (player.hasPermission(permission))
			send("&a✔ " + player.getName() + " has permission " + permission);
		else
			send("&c✗ " + player.getName() + " does not have permission " + permission);
	}

}
