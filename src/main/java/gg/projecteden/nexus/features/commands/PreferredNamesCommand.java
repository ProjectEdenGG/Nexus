package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import lombok.NonNull;

public class PreferredNamesCommand extends CustomCommand {
	private final NerdService service = new NerdService();

	public PreferredNamesCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("[player]")
	@Description("View a player's preferred names")
	void list(@Optional("self") Nerd player) {
		if (player.getPreferredNames().isEmpty())
			error((isSelf(player) ? "You have" : "&e" + player.getNickname() + " has") + " not added any preferred names");

		send(PREFIX + (isSelf(player) ? "Your" : "&e" + player.getNickname() + "'s") + plural(" preferred name", player.getFilteredPreferredNames().size())
			+ ": &e" + String.join("&3, &e", player.getFilteredPreferredNames()));
	}

	@Path("add <name>")
	@Description("Add a preferred name")
	void add(@Arg(stripColor = true) String name) {
		if (name.equals(nickname()))
			error("You cannot added a preferred name that matches your display name");

		if (nerd().getPreferredNames().contains(name))
			error("You have already added &e" + name + " &cas a preferred name");

		nerd().getPreferredNames().add(name);
		service.save(nerd());
		send(PREFIX + "Added preferred name &e" + name);
	}

	@Path("remove <name>")
	@Description("Remove a preferred name")
	void remove(@Arg(stripColor = true) String name) {
		if (nerd().getPreferredNames().remove(name)) {
			service.save(nerd());
			send(PREFIX + "Removed preferred name &e" + name);
		} else
			error("Preferred name &e" + name + " &cnot found");
	}

}
