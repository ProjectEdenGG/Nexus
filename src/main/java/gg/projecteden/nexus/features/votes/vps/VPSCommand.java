package gg.projecteden.nexus.features.votes.vps;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.vote.Voter;
import lombok.NonNull;

public class VPSCommand extends CustomCommand {

	public VPSCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[page]")
	void run(@Arg("1") int page) {
		VPS.open(player(), page);
	}

	@Path("buy plot")
	void buyPlot() {
		if (Nexus.getPerms().playerHas("creative", player(), "plots.plot.6"))
			error("You have already purchased the maximum amount of plots");

		new Voter(player()).takePoints(150);
		runCommandAsConsole("permhelper add plots " + name() + " 1");
		send(PREFIX + "Purchased &e1 creative plot &3for &e150 vote points");
	}

}
