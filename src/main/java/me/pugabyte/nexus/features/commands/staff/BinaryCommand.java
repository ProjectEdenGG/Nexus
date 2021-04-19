package me.pugabyte.nexus.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@Permission("group.staff")
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
