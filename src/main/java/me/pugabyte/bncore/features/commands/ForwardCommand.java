package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.util.Vector;

@Permission("bncore.teleport")
public class ForwardCommand extends CustomCommand {

	public ForwardCommand(CommandEvent event) {
		super(event);
	}

	@Path("<blocks>")
	void forward(@Arg int blocks) {
		Vector forward = player().getEyeLocation().getDirection().multiply(blocks);
		player().teleport(player().getLocation().add(forward));
	}
}
