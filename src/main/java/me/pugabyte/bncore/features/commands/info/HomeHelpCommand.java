package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class HomeHelpCommand extends CustomCommand {

	public HomeHelpCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		line();
		json("&3[+] &c/sethome [homename]||ttp:&eSet a home.\n&3Excluding a home name will set your default \n&3home. It can be teleported to with &c/h||sgt:/sethome ");
		json("&3[+] &c/delhome [homename]||ttp:&eDelete a home.||sgt:/delhome ");
		json("&3[+] &c/h [homename]||ttp:&eTeleport to one of your set homes.\n&3Excluding a home name will teleport \n&3you to your home called '&ehome&3'.||sgt:/h");
		json("&3[+] &c/h <playername> <homename>||ttp:&eTeleport to another player's home.\n&3Please be respectful of people's privacy.||sgt:/h ");
		json("&3[+] &c/homes edit||ttp:&ePrevent people from accessing your \n&ehomes, allow certain people to \n&ebypass the locks, and more!||sgt:/homes edit");
		line();
		json("&e Hover here to see how many homes each rank can set.||ttp:&eNumber of homes each rank can set:\n&7Guest &c3\n&fMember &c4\n&eTrusted &c5\n&6Elite &c6\n&6&lVeteran &c7\n&5Builder &c7\n&5&lArchitect &c8\n&5&oBuild Admin &f& &b&oMG Mod &c9\n&b&oModerator &c10\n&3&oOperator &c11\n&f\n&eRemember, you can buy more in the server store.");
		line();
		json("&3 « &eClick here to return to the help menu.||cmd:/help");
	}

}
