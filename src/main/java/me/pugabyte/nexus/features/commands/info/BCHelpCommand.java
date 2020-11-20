package me.pugabyte.nexus.features.commands.info;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

public class BCHelpCommand extends CustomCommand {

	public BCHelpCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		line(2);
		send("&3These are all the commands available to you in the build contest world.");
		send(json("&3[+] &c/hdb").hover("&eFind decorative heads!").suggest("/hdb"));
		send(json("&3[+] &c/plots home").hover("&eTeleport to your plot").suggest("/plots home"));
		send(json("&3[+] &c/plots setbiome <biome>").hover("&eChange the biome of your plot").suggest("/plots setbiome "));
		send(json("&3[+] &c/plots middle").hover("&&eTeleport to the middle of your current plot").suggest("/plots middle"));
		send(json("&3[+] &c/plots clear").hover("&eClear your plot of all builds").suggest("/plots delete"));
		send(json("&3[+] &c/plots delete").hover("&eClear and unclaim your plot").suggest("/plots home"));
		send(json("&3[+] &c/plots auto").hover("&eClaim a plot").suggest("/plots auto"));
		send(json("&3[+] &c/ci").hover("&eClear your inventory").suggest("/ci"));
		send(json("&3[+] &c/ptime <time>").hover("&eChange the appearance of time.").suggest("/ptime night"));
		send(json("&3[+] &c/speed <speed>").hover("&eChange your walk or fly speed.").suggest("/speed "));
		send(json("&3[+] &c/jump").hover("&eJump forward").suggest("/jump"));
		send(json("&3[+] See the &ecreative commands wiki &3for more info").hover("&eClick to open the wiki").url("https://wiki.bnn.gg/wiki/Commands#Creative"));
		send("&3[+] &eYou can also use WorldEdit, VoxelSniper, and a compass to teleport through walls");
		line();
	}
}
