package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Description("Gives information on the different types of protection offered on the server.")
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
				.next("&6&lLocks").suggest("/lwcinfo").hover("&6&lLock With Commands", "&eProtects chests, doors, &efurnaces, etc", "&eAutomatically applies when you place it").group()
				.next("  &3||  &3")
				.next("&e&lHomes").command("/homes edit").hover("&eUse the GUI to edit your homes.").group()
				.next("  &3||"));
		line();
		send(json("&3 « &eClick here to return to the help menu.").command("/serverinfo"));
	}
}
