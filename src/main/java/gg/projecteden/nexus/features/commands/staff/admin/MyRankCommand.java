package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import lombok.NonNull;

@Permission("set.my.rank")
public class MyRankCommand extends CustomCommand {

	public MyRankCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<rank>")
	void set(Rank rank) {
		for (Rank _rank : Rank.values())
			runCommandAsConsole("lp user " + name() + " parent remove " + _rank.name());
		runCommandAsConsole("lp user " + name() + " parent add " + rank.name());
		send(PREFIX + "Set your rank to " + rank.getColoredName());
	}

}
