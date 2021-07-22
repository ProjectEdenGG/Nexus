package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

public class ProtectionCommand extends CustomCommand {

	public ProtectionCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Override
	public void help() {
		line();
		send(json()
				.next("&3  Which protection type?  &3|| ")
				.next("&6&lLocks").suggest("/lwcinfo").hover("&6&lLock With Commands\n&eProtects chests, doors, \n&efurnaces, etc\n&eAutomatically applies \n&ewhen you place it").group()
				.next("  &3||  &3")
				.next("&e&lHomes").command("/homes edit").hover("&eUse the GUI to edit your homes.").group()
				.next("  &3||"));
		line();
		send(json("&3 « &eClick here to return to the help menu.").command("/serverinfo"));
	}
}
