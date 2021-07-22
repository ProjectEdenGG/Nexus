package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

public class HomeHelpCommand extends CustomCommand {

	public HomeHelpCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Override
	public void help() {
		line();
		send(json("&3[+] &c/sethome [homename]").hover("&eSet a home.\n&3Excluding a home name will set your default \n&3home. It can be teleported to with &c/h").suggest("/sethome "));
		send(json("&3[+] &c/delhome [homename]").hover("&eDelete a home.").suggest("/delhome "));
		send(json("&3[+] &c/h [homename]").hover("&eTeleport to one of your set homes.\n&3Excluding a home name will teleport \n&3you to your home called '&ehome&3'.").suggest("/h"));
		send(json("&3[+] &c/h <playername> <homename>").hover("&eTeleport to another player's home.\n&3Please be respectful of people's privacy.").suggest("/h "));
		send(json("&3[+] &c/homes edit").hover("&ePrevent people from accessing your \n&ehomes, allow certain people to \n&ebypass the locks, and more!").suggest("/homes edit"));
		send(json("&3[+] &c/homes limit").hover("&eView how many homes \n&eyou are able to set").suggest("/homes limit"));
		line();
		send(json("&3 « &eClick here to return to the help menu.").command("/help"));
	}

}
