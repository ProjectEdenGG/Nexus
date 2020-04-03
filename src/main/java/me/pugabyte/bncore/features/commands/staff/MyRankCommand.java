package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.Rank;

@Permission("group.seniorstaff")
public class MyRankCommand extends CustomCommand {

	public MyRankCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<rank>")
	void set(Rank rank) {
		runCommandAsConsole("pex user " + player().getName() + " group set " + rank);
		send(PREFIX + "Set your rank to " + rank.withColor());
	}

}
