package me.pugabyte.bncore.features.commands.worldedit;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.DoubleSlash;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.WorldEditUtils;

@DoubleSlash
@Permission("worldedit.wand")
public class HereVCommand extends CustomCommand {

	public HereVCommand(CommandEvent event) {
		super(event);
	}

	@Path("[amount]")
	void here(@Arg("0") int amount) {
		new WorldEditUtils(player()).setSelection(player(), player().getLocation());
		ExpandVCommand.expandV(player(), amount);
	}
}

