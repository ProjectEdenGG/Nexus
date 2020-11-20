package me.pugabyte.nexus.features.commands.worldedit;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.DoubleSlash;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.WorldEditUtils;

@DoubleSlash
@Permission("worldedit.wand")
public class HereHCommand extends CustomCommand {

	public HereHCommand(CommandEvent event) {
		super(event);
	}

	@Path("[amount]")
	void here(@Arg("0") int amount) {
		new WorldEditUtils(player()).setSelection(player(), player().getLocation());
		ExpandHCommand.expandH(player(), amount);
	}

}
