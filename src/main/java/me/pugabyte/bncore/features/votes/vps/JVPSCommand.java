package me.pugabyte.bncore.features.votes.vps;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Permission("group.seniorstaff")
public class JVPSCommand extends CustomCommand {

	public JVPSCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[page]")
	void run(@Arg("1") int page) {
		VPS.open(player(), page);
	}

}
