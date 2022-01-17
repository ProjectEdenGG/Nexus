package gg.projecteden.nexus.features.commands.worldedit;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.DoubleSlash;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.WorldEditUtils;

@DoubleSlash
@Permission("worldedit.wand")
public class HereHCommand extends CustomCommand {

	public HereHCommand(CommandEvent event) {
		super(event);
	}

	@Path("[amount]")
	void here(@Arg("0") int amount) {
		new WorldEditUtils(player()).setSelection(player(), location());
		ExpandHCommand.expandH(player(), amount);
	}

}
