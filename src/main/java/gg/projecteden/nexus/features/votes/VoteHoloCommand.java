package gg.projecteden.nexus.features.votes;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

import static gg.projecteden.nexus.features.listeners.Restrictions.isPerkAllowedAt;

// TODO Create own implementation using HD API instead of running HD commands

@Permission("vote.holo")
public class VoteHoloCommand extends CustomCommand {

	public VoteHoloCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("create <text...>")
	void create(String text) {
		if (!isPerkAllowedAt(player(), location()))
			error("Holograms cannot be created here");

		runCommandAsOp("hd create voteholo_" + uuid() + " " + text);
		send(PREFIX + "Created. Edit with &c/voteholo edit <text...>");
	}

	@Path("edit <text...>")
	void edit(String text) {
		runCommandAsConsole("hd setline voteholo_" + uuid() + " 1 " + text);
		send(PREFIX + "Edited");
	}

	@Path("delete")
	void delete() {
		runCommandAsConsole("hd delete voteholo_" + uuid());
		send(PREFIX + "Deleted");
	}

	@Path("tphere")
	void tphere() {
		if (!isPerkAllowedAt(player(), location()))
			error("Holograms cannot be teleported here");

		runCommandAsOp("hd movehere voteholo_" + uuid());
	}
}
