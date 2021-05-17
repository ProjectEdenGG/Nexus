package me.pugabyte.nexus.features.commands.staff.operator;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@Permission("group.seniorstaff")
public class MoreCommand extends CustomCommand {

	public MoreCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		getToolRequired().setAmount(64);
	}

}
