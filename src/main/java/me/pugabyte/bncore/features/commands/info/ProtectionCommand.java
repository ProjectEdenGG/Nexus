package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class ProtectionCommand extends CustomCommand {

	public ProtectionCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		line();
		send(json()
				.next("&3  Which protection type?  &3|| ")
				.next("&b&lP-Stones").suggest("/pstoneinfo").hover("&b&lProtection Stones\n&eCoal, Lapis, Diamond, and Emerald ores\n&eProtects all blocks & animals inside\n&ethe field").group()
				.next("  &3||  &3")
				.next("&6&lLWC").suggest("/lwcinfo").hover("&6&lLock With Commands\n&eProtects chests, doors, \n&efurnaces, etc\n&eAutomatically applies \n&ewhen you place it").group()
				.next("  &3||  &3")
				.next("&e&lHomes").command("/homes edit").hover("&eUse the GUI to edit your homes.").group()
				.next("  &3||"));
		line();
		send(json("&3 « &eClick here to return to the help menu.").command("/serverinfo"));
	}
}
