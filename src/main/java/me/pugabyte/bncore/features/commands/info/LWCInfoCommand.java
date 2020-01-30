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
		json("&3[+] &c/cmodify <playername>||ttp:&eGive a player access to a private protection.||sgt:/cmodify ");
		json("&3[+] &c/cmodifyall <playername>||ttp:&eModify all your protections||sgt:/cmodifyall ");
		json("&3[+] &c/cmodifynear <radius> <playername>||ttp:&eModify all your protections within a radius.||sgt:/cmodifynear ");
		json("&3[+] &c/cmodify &4-&c<playername>||ttp:&eRemove a player's access to a protection.||sgt:/cmodify ");
		json("&3[+] &c/ccopy||ttp:&eCopy one protection's configuration onto another.||sgt:/ccopy");
		json("&3[+] &c/hopper||ttp:&eEnable hoppers on your protection. " +
				"\n&3(Disabled by default because &3people can use " +
				"\n&3hoppers to steal from a locked chest.) " +
				"\n&cUse this on the chest/furnace/etc, not the hopper.||sgt:/hopper");
		json("&3[+] &c/crepeat||ttp:&eMake commands automatically repeat " +
				"\n&ewithout having to type them out again. " +
				"\n&3Useful when allowing a modifying many " +
				"\n&3protections in the same way.||sgt:/crepeat");
		json("&3[+] &c/cunlock &c<password>||ttp:&eAccess a password protected protection with the password.||sgt:/cunlock ");
		json("&3[+] &c/cremove||ttp:&eRemove a protection completely. " +
				"\n&3You must first run this command to " +
				"\n&3make any of the following protections.||sgt:/cremove");
		send(" &4Note: &eYou must use &c/cremove &ebefore any of the following");
		json("&3[+] &c/cdonation||ttp:&eCreates a donation chest. " +
				"\n&3Players can put items in, but only you can remove them.||sgt:/cdonation");
		json("&3[+] &c/cpassword &c<password>||ttp:&eCreates a password for the protection. " +
				"\n&3Anyone who knows the password have access to it.||sgt:/cpassword ");
		json("&3[+] &c/cpublic||ttp:&eCreates a public protection. " +
				"\n&3You still own the chest, so no one else " +
				"\n&3can lock it, but anyone can access it.||sgt:/cpublic");
		json("&3[+] &c/lock||ttp:&eRecreates a private protection.||cmd:/lock");
		line();
		json("&3 « &eClick here to return to the help menu.||cmd:/serverinfo");

	}

	@Path()
	void help() {
		send("&6&lLWC &3protects all &estorage &3and &edoor related &3blocks from being used by other players without your permission.");
		line();
		send("&3A private protection is created &eautomatically&3. Change it by &eright clicking &3on the protection &eafter &3using the command.");
		line();
		send("&3More information about LWC &3(&eClick to go&3)");
		json("&3[+] &eProtected items list &3(&eHover over me!&3)||ttp:&eChests" +
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
				"\n&eBanners (&3lock manually&e)");
		json("&3[+] &eCommands||cmd:/lwcinfo commands");
		line();
		json("&3 « &eClick here to return to the help menu.||cmd:/serverinfo");
	}
}
