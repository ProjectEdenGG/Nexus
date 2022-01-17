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

