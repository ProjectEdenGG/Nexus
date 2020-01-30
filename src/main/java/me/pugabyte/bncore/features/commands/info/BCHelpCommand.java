package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class BCHelpCommand extends CustomCommand {

	public BCHelpCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		line(2);
		send("&3These are all the commands available to you in the build contest world.");
		json("&3[+] &c/hdb||ttp:&eFind decorative heads!||sgt:/hdb");
		json("&3[+] &c/plots home||ttp:&eTeleport to your plot||sgt:/plots home");
		json("&3[+] &c/plots setbiome <biome>||ttp:&eChange the biome of your plot||sgt:/plots setbiome ");
		json("&3[+] &c/plots middle||ttp:&&eTeleport to the middle of your current plot||sgt:/plots middle");
		json("&3[+] &c/plots clear||ttp:&eClear your plot of all builds||sgt:/plots delete");
		json("&3[+] &c/plots delete||ttp:&eClear and unclaim your plot||sgt:/plots home");
		json("&3[+] &c/plots auto||ttp:&eClaim a plot||sgt:/plots auto");
		json("&3[+] &c/ci||ttp:&eClear your inventory||sgt:/ci");
		json("&3[+] &c/ptime <time>||ttp:&eChange the appearance of time.||sgt:/ptime night");
		json("&3[+] &c/speed <speed>||ttp:&eChange your walk or fly speed.||sgt:/speed ");
		json("&3[+] &c/jump||ttp:&eJump forward||sgt:/jump");
		json("&3[+] See the &ecreative commands wiki &3for more info||ttp:&eClick to open the wiki||url:https://wiki.bnn.gg/wiki/Commands#Creative");
		send("&3[+] &eYou can also use WorldEdit, VoxelSniper, and a compass to teleport through walls");
		line();
	}
}
