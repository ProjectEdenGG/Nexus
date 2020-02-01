package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class LWCInfoCommand extends CustomCommand {

	public LWCInfoCommand(CommandEvent event) {
		super(event);
	}

	@Path("(cmd|commands)")
	void cmd() {
		line();
		send(json2("&3[+] &c/cmodify <playername>").hover("&eGive a player access to a private protection.").suggest("/cmodify "));
		send(json2("&3[+] &c/cmodifyall <playername>").hover("&eModify all your protections").suggest("/cmodifyall "));
		send(json2("&3[+] &c/cmodifynear <radius> <playername>").hover("&eModify all your protections within a radius.").suggest("/cmodifynear "));
		send(json2("&3[+] &c/cmodify &4-&c<playername>").hover("&eRemove a player's access to a protection.").suggest("/cmodify "));
		send(json2("&3[+] &c/ccopy").hover("&eCopy one protection's configuration onto another.").suggest("/ccopy"));
		send(json2("&3[+] &c/hopper").hover("&eEnable hoppers on your protection. " +
				"\n&3(Disabled by default because &3people can use " +
				"\n&3hoppers to steal from a locked chest.) " +
				"\n&cUse this on the chest/furnace/etc, not the hopper.").suggest("/hopper"));
		send(json2("&3[+] &c/crepeat").hover("&eMake commands automatically repeat " +
				"\n&ewithout having to type them out again. " +
				"\n&3Useful when allowing a modifying many " +
				"\n&3protections in the same way.").suggest("/crepeat"));
		send(json2("&3[+] &c/cunlock &c<password>").hover("&eAccess a password protected protection with the password.").suggest("/cunlock "));
		send(json2("&3[+] &c/cremove").hover("&eRemove a protection completely. " +
				"\n&3You must first run this command to " +
				"\n&3make any of the following protections.").suggest("/cremove"));
		send(" &4Note: &eYou must use &c/cremove &ebefore any of the following");
		send(json2("&3[+] &c/cdonation").hover("&eCreates a donation chest. " +
				"\n&3Players can put items in, but only you can remove them.").suggest("/cdonation"));
		send(json2("&3[+] &c/cpassword &c<password>").hover("&eCreates a password for the protection. " +
				"\n&3Anyone who knows the password have access to it.").suggest("/cpassword "));
		send(json2("&3[+] &c/cpublic").hover("&eCreates a public protection. " +
				"\n&3You still own the chest, so no one else " +
				"\n&3can lock it, but anyone can access it.").suggest("/cpublic"));
		send(json2("&3[+] &c/lock").hover("&eRecreates a private protection.").command("/lock"));
		line();
		send(json2("&3 « &eClick here to return to the help menu.").command("/serverinfo"));

	}

	@Path()
	void help() {
		send("&6&lLWC &3protects all &estorage &3and &edoor related &3blocks from being used by other players without your permission.");
		line();
		send("&3A private protection is created &eautomatically&3. Change it by &eright clicking &3on the protection &eafter &3using the command.");
		line();
		send("&3More information about LWC &3(&eClick to go&3)");
		send(json2("&3[+] &eProtected items list &3(&eHover over me!&3)").hover("&eChests" +
				"\n&eTrapped Chests" +
				"\n&eFurances" +
				"\n&eDispensers" +
				"\n&eDroppers" +
				"\n&eSigns (&3lock manually&e)" +
				"\n&eAll doors" +
				"\n&eAll fence gates" +
				"\n&eAll trap doors" +
				"\n&eBeacons" +
				"\n&eHoppers" +
				"\n&eAnvils" +
				"\n&eBanners (&3lock manually&e)"));
		send(json2("&3[+] &eCommands").command("/lwcinfo commands"));
		line();
		send(json2("&3 « &eClick here to return to the help menu.").command("/serverinfo"));
	}
}
