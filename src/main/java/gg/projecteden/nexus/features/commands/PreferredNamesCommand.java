package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.chat.Censor;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;

public class PreferredNamesCommand extends CustomCommand {
	private final NerdService service = new NerdService();

	public PreferredNamesCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	@Description("View a player's preferred names")
	void list(@Arg("self") Nerd player) {
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

		if (Censor.isCensored(player(), name)) {
			String message = "&cPreferred name of " + nickname() + " was censored: &e" + name;
			Broadcast.staff().prefix("Censor").message(message).send();
			error("Inappropriate input");
			return;
		}

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
