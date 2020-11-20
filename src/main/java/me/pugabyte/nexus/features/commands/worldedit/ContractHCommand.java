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
public class ContractHCommand extends CustomCommand {

	public ContractHCommand(CommandEvent event) {
		super(event);
	}

	@Path("[amount]")
	void contractH(@Arg("1") int amount) {
		new WorldEditUtils(player()).changeSelection(
				player(),
				WorldEditUtils.SelectionChangeType.CONTRACT,
				WorldEditUtils.SelectionChangeDirectionType.HORIZONTAL,
				amount
		);
	}
}

