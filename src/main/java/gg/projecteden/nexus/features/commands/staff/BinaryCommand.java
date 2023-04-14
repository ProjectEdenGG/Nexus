package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Vararg;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import lombok.NonNull;

@HideFromWiki
@Permission(Group.STAFF)
public class BinaryCommand extends CustomCommand {

	public BinaryCommand(@NonNull CommandEvent event) {
		super(event);
	}

	void to(@Vararg String input) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			StringBuilder character = new StringBuilder(Integer.toBinaryString(input.charAt(i)));
			for (int j = character.length(); j < 8; j++)
				character.insert(0, "0");
			result.append(character).append(" ");
		}

		send(json("&3Result: &e" + result).suggest(result.toString()));
	}

	void from(@Vararg String input) {
		StringBuilder result = new StringBuilder();
		String[] binary = input.split(" ");
		for (String s : binary)
			result.append((char) Integer.parseInt(s, 2));

		send(json("&3Result: &e" + result).suggest(result.toString()));
	}

}
