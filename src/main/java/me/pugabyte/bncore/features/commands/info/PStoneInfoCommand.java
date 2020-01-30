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
		json("&3 « &eClick here to return to the Protection Stone menu.||cmd:/pstoneinfo");
	}

	@Path("(cmds|commands)")
	void commands() {
		line();
		send("&eProtection Stone Commands &3(&eHover for more info!&3)");
		json("&3[+] &c/ps allow <player>||ttp:&eAllow another player to your edit inside Protection Stone field||sgt:/ps allow ");
		json("&3[+] &c/ps remove <player>||ttp:&eRemove another player's access to your field||sgt:/ps remove ");
		json("&3[+] &c/ps info||ttp:&eView information about the field." +
				"\n&3The same output is given by right clicking " +
				"\n&3on a field block with a diamond tool.||sgt:/ps info");
		json("&3[+] &c/ps visualize||ttp:&eCreate a temporary glass box around " +
				"\n&eyour field, showing you what is protected." +
				"\n&3Only the blocks &cinside &3the glass are protected.||sgt:/ps visualize");
		json("&3[+] &c/ps take||ttp:&eRemove the protection block and place it in your inventory.||sgt:/ps take");
		json("&3[+] &c/ps allowed||ttp:&eGet a list of who is allowed to the field||sgt:/ps allowed");
		json("&3[+] &c/ps mark||ttp:&eCreate temporary obsidian towers at the " +
				"\n&elocations of your nearby Protection Stones||sgt:/ps mark");
		json("&3[+] &c/ps toggle prevent-flow||ttp:&eToggle the water flow prevention flag." +
				"\n&3By default, Protection Stones prevent water from " +
				"\n&3flowing in or out of your field, to prevent " +
				"\n&3water grief. If you have water crossing the border, " +
				"\n&3you will need to toggle this, get the water flowing, " +
				"\n&3and then (recommended) toggle it again.||sgt:/ps toggle prevent-flow");
		json("&3[+] &c/freepstone||ttp:&eReceive your free monthly Coal PStone||sgt:/freepstone");
		json("&3[+] &c/ps setvelocity <.1-5>||ttp:&eSet the power of your Skycannon &3(&e0=auto&3)||sgt:/ps setvelocity ");
		line();
		json("&3 « &eClick here to return to the Protection Stone menu.||cmd:/pstoneinfo");

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
		json("&3 « &eClick here to return to the Protection Stone menu.||cmd:/pstoneinfo");
	}

	@Path("sap")
	void sap() {
		line();
		send("&eProtection Stone Sizes & Prices. &3(&eHover for more info!&3) " +
				"\n");
		send("&7Coal Ore");
		json("      &eSize: &311x11x11||ttp:&3(&eRadius of 5&3)");
		json("      &ePrice: &3$650||ttp:&3(&e$.50/block&3)");
		send("" +
				"\n &9Lapis Ore");
		json("      &eSize: &321x21x21||ttp:&3(&eRadius of 10&3)");
		send("" +
				"\n &bDiamond Ore");
		json("      &eSize: &341x41x41||ttp:&3(&eRadius of 20&3)");
		json("      &ePrice: &3$24,100||ttp:&3(&e$.35/block&3)");
		send("" +
				"\n &aEmerald Ore");
		json("      &eSize: &381x81x81||ttp:&3(&eRadius of 40&3)");
		json("      &ePrice: &3$132,500||ttp:&3(&e$.25/block&3)");
		line();
		json("&3 « &eClick here to return to the Protection Stone menu.||cmd:/pstoneinfo");
	}

	@Path
	void help() {
		line();
		send("&3Our server offers &eProtection Stones&3, which will protect your land a &ecertain radius &3from the protection ore. Only you can edit blocks inside the protection zone. &7Guests &ecannot &3use protection stones, &eonly &fMembers &3and above.");
		line();
		send("&3But &edon't worry &3if you can't afford them or if you are not a member, griefing is still &eagainst the rules&3, and a staff member will happily fix any grief for you.");
		line();
		send("&3More information about PStones &3(&eClick to go&3)");
		json("&3[+] &eSizes & Prices||cmd:/pstoneinfo sap");
		json("&3[+] &eGetting started||cmd:/pstoneinfo gs");
		json("&3[+] &ePStone features||cmd:/pstoneinfo func");
		json("&3[+] &eCommands||cmd:/pstoneinfo cmds");
		json("&3 « &eClick here to return to the Protection menu.||cmd:/protection");

	}

}
