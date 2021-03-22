package me.pugabyte.nexus.features.commands.staff.admin;

import lombok.NonNull;
import lombok.SneakyThrows;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

import java.io.InputStream;
import java.util.Scanner;

@Permission("group.admin")
public class BashCommand extends CustomCommand {

	public BashCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<command...>")
	void run(String command) {
		try {
			String result = execute(command);
			if (isNullOrEmpty(result))
				send(PREFIX + "Command executed successfully");
			else
				send(PREFIX + "&7" + result);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@SneakyThrows
	public static String execute(String command) {
		InputStream result = Runtime.getRuntime().exec(command).getInputStream();
		StringBuilder builder = new StringBuilder();
		new Scanner(result).forEachRemaining(string -> builder.append(string).append(" "));
		return builder.toString().trim();
	}

}
