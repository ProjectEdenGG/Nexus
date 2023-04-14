package gg.projecteden.nexus.features.commands.worldedit;

import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.DoubleSlash;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.utils.WorldEditUtils;

@DoubleSlash
@Permission("worldedit.wand")
public class HereHCommand extends CustomCommand {

	public HereHCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Set your selection to your current location and optionally expand it horizontally")
	void here(@Optional int amount) {
		new WorldEditUtils(player()).setSelection(player(), location());
		ExpandHCommand.expandH(player(), amount);
	}

}
