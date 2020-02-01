package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class PStoneInfoCommand extends CustomCommand {

	public PStoneInfoCommand(CommandEvent event) {
		super(event);
	}

	@Path("func")
	void func() {
		line();
		send("&3What &eelse &3can protection stones do?");
		line();
		send("&3Protection stones protect &eall &3blocks, crops, anything you can think of. They also protect &eanimals &3from being harmed by other players, &earmour stands &3from being stolen, and prevent &eitem frames &3from being accessed!");
		line();
		send(json2("&3 « &eClick here to return to the Protection Stone menu.").command("/pstoneinfo"));
	}

	@Path("(cmds|commands)")
	void commands() {
		line();
		send("&eProtection Stone Commands &3(&eHover for more info!&3)");
		send(json2("&3[+] &c/ps allow <player>").hover("&eAllow another player to your edit inside Protection Stone field").suggest("/ps allow "));
		send(json2("&3[+] &c/ps remove <player>").hover("&eRemove another player's access to your field").suggest("/ps remove "));
		send(json2("&3[+] &c/ps info").hover("&eView information about the field." +
				"\n&3The same output is given by right clicking " +
				"\n&3on a field block with a diamond tool.").suggest("/ps info"));
		send(json2("&3[+] &c/ps visualize").hover("&eCreate a temporary glass box around " +
				"\n&eyour field, showing you what is protected." +
				"\n&3Only the blocks &cinside &3the glass are protected.").suggest("/ps visualize"));
		send(json2("&3[+] &c/ps take").hover("&eRemove the protection block and place it in your inventory.").suggest("/ps take"));
		send(json2("&3[+] &c/ps allowed").hover("&eGet a list of who is allowed to the field").suggest("/ps allowed"));
		send(json2("&3[+] &c/ps mark").hover("&eCreate temporary obsidian towers at the " +
				"\n&elocations of your nearby Protection Stones").suggest("/ps mark"));
		send(json2("&3[+] &c/ps toggle prevent-flow").hover("&eToggle the water flow prevention flag." +
				"\n&3By default, Protection Stones prevent water from " +
				"\n&3flowing in or out of your field, to prevent " +
				"\n&3water grief. If you have water crossing the border, " +
				"\n&3you will need to toggle this, get the water flowing, " +
				"\n&3and then (recommended) toggle it again.").suggest("/ps toggle prevent-flow"));
		send(json2("&3[+] &c/freepstone").hover("&eReceive your free monthly Coal PStone").suggest("/freepstone"));
		send(json2("&3[+] &c/ps setvelocity <.1-5>").hover("&eSet the power of your Skycannon &3(&e0=auto&3)").suggest("/ps setvelocity "));
		line();
		send(json2("&3 « &eClick here to return to the Protection Stone menu.").command("/pstoneinfo"));

	}

	@Path("gs")
	void gs() {
		line();
		send("&fMembers &3can receive &eone free coal protection stone &3each month by typing &c/freepstone&3. You can also buy additional protection stones at the &c/market&3.");
		line();
		send("&3Once you have a protection stone, place it in the &emiddle &3of what you want &eprotected&3. It will automatically show what it protects by creating a temporary &eglass box&3, but you can type &c/ps visualize &3to show the glass again. If you want to &emove &3it, use the command &c/ps take&3.");
		line();
		send("&3Protection Stones are purposely &eoverpriced &3to prevent mass protection of land that will not be used.");
		line();
		send(json2("&3 « &eClick here to return to the Protection Stone menu.").command("/pstoneinfo"));
	}

	@Path("sap")
	void sap() {
		line();
		send("&eProtection Stone Sizes && Prices. &3(&eHover for more info!&3) " +
				"\n");
		send(" &7Coal Ore");
		send(json2("      &eSize: &311x11x11").hover("&3(&eRadius of 5&3)"));
		send(json2("      &ePrice: &3$650").hover("&3(&e$.50/block&3)"));
		send("" +
				"\n &9Lapis Ore");
		send(json2("      &eSize: &321x21x21").hover("&3(&eRadius of 10&3)"));
		send("" +
				"\n &bDiamond Ore");
		send(json2("      &eSize: &341x41x41").hover("&3(&eRadius of 20&3)"));
		send(json2("      &ePrice: &3$24,100").hover("&3(&e$.35/block&3)"));
		send("" +
				"\n &aEmerald Ore");
		send(json2("      &eSize: &381x81x81").hover("&3(&eRadius of 40&3)"));
		send(json2("      &ePrice: &3$132,500").hover("&3(&e$.25/block&3)"));
		line();
		send(json2("&3 « &eClick here to return to the Protection Stone menu.").command("/pstoneinfo"));
	}

	@Path
	void help() {
		line();
		send("&3Our server offers &eProtection Stones&3, which will protect your land a &ecertain radius &3from the protection ore. Only you can edit blocks inside the protection zone. &7Guests &ecannot &3use protection stones, &eonly &fMembers &3and above.");
		line();
		send("&3But &edon't worry &3if you can't afford them or if you are not a member, griefing is still &eagainst the rules&3, and a staff member will happily fix any grief for you.");
		line();
		send("&3More information about PStones &3(&eClick to go&3)");
		send(json2("&3[+] &eSizes &&e Prices").command("/pstoneinfo sap"));
		send(json2("&3[+] &eGetting started").command("/pstoneinfo gs"));
		send(json2("&3[+] &ePStone features").command("/pstoneinfo func"));
		send(json2("&3[+] &eCommands").command("/pstoneinfo cmds"));
		send(json2("&3 « &eClick here to return to the Protection menu.").command("/protection"));

	}

}
