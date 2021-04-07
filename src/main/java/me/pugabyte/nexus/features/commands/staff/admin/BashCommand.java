package me.pugabyte.nexus.features.commands.staff.admin;

import com.google.common.base.Strings;
import lombok.NonNull;
import lombok.SneakyThrows;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.StringUtils;

import java.io.InputStream;
import java.util.Scanner;

@Permission("group.admin")
public class BashCommand extends CustomCommand {

	public BashCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<command...>")
	@Async
	void run(String command) {
		send(tryExecute(command));
	}

	public static String tryExecute(String command) {
		String PREFIX = StringUtils.getPrefix("Bash");
		try {
			String result = execute(command);
			if (Strings.isNullOrEmpty(result))
				return PREFIX + "Command executed successfully";
			else
				return PREFIX + "&7" + result;
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
