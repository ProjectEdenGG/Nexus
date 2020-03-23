package me.pugabyte.bncore.features.store.perks;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class SortCommand extends CustomCommand {

	public SortCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("depositall");
	}

}
