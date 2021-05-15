package me.pugabyte.nexus.features.votes;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

import static me.pugabyte.nexus.features.listeners.Restrictions.isPerkAllowedAt;

// TODO Create own implementation using HD API instead of running HD commands

@Permission("vote.holo")
public class VoteHoloCommand extends CustomCommand {

	public VoteHoloCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("create <text...>")
	void create(String text) {
		if (!isPerkAllowedAt(location()))
			error("Holograms cannot be created here");

		runCommandAsOp("hd create voteholo_" + uuid().toString() + " " + text);
		send(PREFIX + "Created. Edit with &c/voteholo edit <text...>");
	}

	@Path("edit <text...>")
	void edit(String text) {
		runCommandAsConsole("hd setline voteholo_" + uuid().toString() + " 1 " + text);
		send(PREFIX + "Edited");
	}

	@Path("delete")
	void delete() {
		runCommandAsConsole("hd delete voteholo_" + uuid().toString());
		send(PREFIX + "Deleted");
	}

	@Path("tphere")
	void tphere() {
		if (!isPerkAllowedAt(location()))
			error("Holograms cannot be teleported here");

		runCommandAsOp("hd movehere voteholo_" + uuid().toString());
	}
}
