package me.pugabyte.nexus.features.store.perks;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

public class SortCommand extends CustomCommand {

	public SortCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("depositall");
	}

}
