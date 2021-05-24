package me.pugabyte.nexus.features.votes.vps;

import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.vote.Voter;

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
