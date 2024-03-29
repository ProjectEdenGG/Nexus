package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

public class LWCInfoCommand extends CustomCommand {

	public LWCInfoCommand(CommandEvent event) {
		super(event);
	}

	@Path("(cmd|commands)")
	@Description("Learn about LWC and the commands it offers")
	void cmd() {
		line();
		send(json("&3[+] &c/trust lock <playername>").hover("&eGive a player access to a private protection.").suggest("/trust lock "));
		send(json("&3[+] &c/trust locks <playername>").hover("&eModify all your protections").suggest("/trust locks "));
//		send(json("&3[+] &c/cmodifynear <radius> <playername>").hover("&eModify all your protections within a radius.").suggest("/cmodifynear "));
		send(json("&3[+] &c/untrust lock <playername>").hover("&eRemove a player's access to a protection.").suggest("/untrust lock "));
		send(json("&3[+] &c/ccopy").hover("&eCopy one protection's configuration onto another.").suggest("/ccopy"));
		send(json("&3[+] &c/hopper").hover(
			"&eEnable hoppers on your protection.",
			"&3(Disabled by default because &3people can use",
			"&3hoppers to steal from a locked chest.)",
			"&cUse this on the chest/furnace/etc, not the hopper."
		).suggest("/hopper"));
		send(json("&3[+] &c/crepeat").hover(
			"&eMake commands automatically repeat",
			"&ewithout having to type them out again.",
			"&3Useful when allowing a modifying many",
			"&3protections in the same way."
		).suggest("/crepeat"));
		send(json("&3[+] &c/cremove").hover(
			"&eRemove a protection completely.",
			"&3You must first run this command to",
			"&3make any of the following protections."
		).suggest("/cremove"));
		send(" &4Note: &eYou must use &c/cremove &ebefore any of the following");
		send(json("&3[+] &c/cdonation").hover(
			"&eCreates a donation chest.",
			"&3Players can put items in, but only you can remove them."
		).suggest("/cdonation"));
		send(json("&3[+] &c/cpublic").hover(
			"&eCreates a public protection.",
			"&3You still own the chest, so no one else",
			"&3can lock it, but anyone can access it."
		).suggest("/cpublic"));
		send(json("&3[+] &c/lock").hover("&eRecreates a private protection.").command("/lock"));
		line();
		send(json("&3 « &eClick here to return to the help menu.").command("/serverinfo"));

	}

	@Path
	@Description("Sends information on the LWC plugin and what it's used for")
	@Override
	public void help() {
		send("&6&lLWC &3protects all &estorage &3and &edoor related &3blocks from being used by other players without your permission.");
		line();
		send("&3A private protection is created &eautomatically&3. Change it by &eright clicking &3on the protection &eafter &3using the command.");
		line();
		send("&3More information about LWC &3(&eClick to go&3)");
		send(json("&3[+] &eProtected items list &3(&eHover over me!&3)").hover(
			"&eChests",
			"&eTrapped Chests",
			"&eFurnaces",
			"&eDispensers",
			"&eDroppers",
			"&eSigns (&3lock manually&e)",
			"&eAll doors",
			"&eAll fence gates",
			"&eAll trap doors",
			"&eBeacons",
			"&eHoppers",
			"&eAnvils",
			"&eBanners (&3lock manually&e)"
		));
		send(json("&3[+] &eCommands").command("/lwcinfo commands"));
		line();
		send(json("&3 « &eClick here to return to the help menu.").command("/serverinfo"));
	}
}
