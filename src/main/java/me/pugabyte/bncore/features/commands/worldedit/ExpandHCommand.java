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
public class ExpandHCommand extends CustomCommand {

	public ExpandHCommand(CommandEvent event) {
		super(event);
	}

	@Path("[amount]")
	void expandH(@Arg("1") int amount) {
		new WorldEditUtils(player()).changeSelection(
				player(),
				WorldEditUtils.SelectionChangeType.EXPAND,
				WorldEditUtils.SelectionChangeDirectionType.HORIZONTAL,
				amount
		);
	}
}

