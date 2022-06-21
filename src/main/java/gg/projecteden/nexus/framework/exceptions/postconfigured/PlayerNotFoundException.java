package gg.projecteden.nexus.framework.exceptions.postconfigured;

public class PlayerNotFoundException extends gg.projecteden.api.common.exceptions.postconfigured.PlayerNotFoundException {

	public PlayerNotFoundException(String input) {
		super("&e" + input + "&c");
	}

}
