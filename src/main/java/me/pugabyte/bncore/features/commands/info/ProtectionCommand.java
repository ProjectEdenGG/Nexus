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
		json("&3  Which protection type?  &3|&3| " + "||&b&lP-Stones||sgt:/pstoneinfo||ttp:&b&lProtection Stones\n&eCoal, Lapis, Diamond, and Emerald ores\n&eProtects all blocks & animals inside\n&ethe field" +
				"||  &3|&3|  " + "||&6&lLWC||sgt:/lwcinfo||ttp:&6&lLock With Commands\n&eProtects chests, doors, \n&efurnaces, etc\n&eAutomatically applies \n&ewhen you place it" +
				"||  &3|&3|  " + "||&e&lHomes||cmd:/homes edit||ttp:&eUse the GUI to edit your homes." +
				"||  &3|&3|");
		line();
		json("&3 « &eClick here to return to the help menu.||cmd:/serverinfo");
	}
}
