package me.pugabyte.bncore.features.commands.poof;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Fallback;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

@Aliases("s")
@Fallback("essentials")
public class TPHereCommand extends CustomCommand {

	public TPHereCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void run(Player player) {
		if (!player().hasPermission("essentials.tphere"))
			runCommand("tpahere " + argsString());
		else
			fallback();
	}

}
