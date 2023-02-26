package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

@HideFromWiki
@Permission(Group.STAFF)
public class BinaryCommand extends CustomCommand {

	public BinaryCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("to <text...>")
	void to(String input) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			StringBuilder character = new StringBuilder(Integer.toBinaryString(input.charAt(i)));
			for (int j = character.length(); j < 8; j++)
				character.insert(0, "0");
			result.append(character).append(" ");
		}

		send(json("&3Result: &e" + result).suggest(result.toString()));
	}

	@Path("from <binary...>")
	void from(String input) {
		StringBuilder result = new StringBuilder();
		String[] binary = input.split(" ");
		for (String s : binary)
			result.append((char) Integer.parseInt(s, 2));

		send(json("&3Result: &e" + result).suggest(result.toString()));
	}

}
