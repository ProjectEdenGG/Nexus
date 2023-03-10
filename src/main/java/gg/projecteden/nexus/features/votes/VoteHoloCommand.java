package gg.projecteden.nexus.features.votes;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

import static gg.projecteden.nexus.features.listeners.Restrictions.isPerkAllowedAt;

// TODO Create own implementation using HD API instead of running HD commands

@Permission("vote.holo")
@Description("Modify the hologram you received from vote rewards")
@WikiConfig(rank = "Guest", feature = "Vote")
public class VoteHoloCommand extends CustomCommand {

	public VoteHoloCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("create <text...>")
	@Description("Create a hologram")
	void create(String text) {
		if (!isPerkAllowedAt(player(), location()))
			error("Holograms cannot be created here");

		runCommandAsOp("hd create voteholo_" + uuid() + " " + text);
		send(PREFIX + "Created. Edit with &c/voteholo edit <text...>");
	}

	@Path("edit <text...>")
	@Description("Edit your hologram")
	void edit(String text) {
		runCommandAsConsole("hd setline voteholo_" + uuid() + " 1 " + text);
		send(PREFIX + "Edited");
	}

	@Path("delete")
	@Description("Delete your hologram")
	void delete() {
		runCommandAsConsole("hd delete voteholo_" + uuid());
		send(PREFIX + "Deleted");
	}

	@Path("tphere")
	@Description("Summon your hologram")
	void tphere() {
		if (!isPerkAllowedAt(player(), location()))
			error("Holograms cannot be teleported here");

		runCommandAsOp("hd movehere voteholo_" + uuid());
	}
}
