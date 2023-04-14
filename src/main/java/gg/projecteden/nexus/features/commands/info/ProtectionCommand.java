package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;

public class ProtectionCommand extends CustomCommand {

	public ProtectionCommand(CommandEvent event) {
		super(event);
	}

	@Override
	@NoLiterals
	@Description("View the different types of protection offered on the server")
	public void help() {
		line();
		send(json()
				.next("&3  Which protection type?  &3|| ")
				.next("&6&lLocks").suggest("/lwcinfo").hover("&6&lLock With Commands", "&eProtects chests, doors, &efurnaces, etc", "&eAutomatically applies when you place it").group()
				.next("  &3||  &3")
				.next("&e&lHomes").command("/homes edit").hover("&eUse the GUI to edit your homes.").group()
				.next("  &3||"));
		line();
		send(json("&3 « &eClick here to return to the help menu.").command("/serverinfo"));
	}
}
