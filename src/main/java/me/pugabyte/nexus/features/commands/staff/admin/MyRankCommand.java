package me.pugabyte.nexus.features.commands.staff.admin;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Rank;

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
