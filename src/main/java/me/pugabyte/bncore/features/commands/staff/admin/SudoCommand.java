package me.pugabyte.bncore.features.commands.staff.admin;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;

@Permission("group.admin")
public class SudoCommand extends CustomCommand {

	public SudoCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player> <command...>")
	void run(Player player, String command) {
		Utils.runCommandAsOp(player, command);
		send("&3Made &e" + player.getName() + " &3run &e/" + command);
	}

}
