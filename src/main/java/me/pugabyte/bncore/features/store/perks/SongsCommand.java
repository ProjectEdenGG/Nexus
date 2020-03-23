package me.pugabyte.bncore.features.store.perks;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Aliases("sogns")
public class SongsCommand extends CustomCommand {

	public SongsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("powder songs");
	}

}
