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
public class ExpandVCommand extends CustomCommand {

	public ExpandVCommand(CommandEvent event) {
		super(event);
	}

	@Path("[amount]")
	void expandV(@Arg("1") int amount) {
		new WorldEditUtils(player()).changeSelection(
				player(),
				WorldEditUtils.SelectionChangeType.EXPAND,
				WorldEditUtils.SelectionChangeDirectionType.VERTICAL,
				amount
		);
	}

}

