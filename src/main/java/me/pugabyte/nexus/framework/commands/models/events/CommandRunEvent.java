package me.pugabyte.nexus.framework.commands.models.events;

import eden.exceptions.EdenException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.Commands;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Description;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.exceptions.NexusException;
import me.pugabyte.nexus.framework.exceptions.preconfigured.MissingArgumentException;
import me.pugabyte.nexus.utils.JsonBuilder;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

@Data
@RequiredArgsConstructor
public class CommandRunEvent extends CommandEvent {
	private Method method;
	private String usage;

	public CommandRunEvent(CommandSender sender, CustomCommand command, String aliasUsed, List<String> args, List<String> originalArgs) {
		super(sender, command, aliasUsed, args, originalArgs);
	}

	public void setUsage(Method method) {
		this.method = method;
		Path path = method.getAnnotation(Path.class);
		if (path != null) {
			this.usage = path.value();
			Description desc = method.getAnnotation(Description.class);
			if (desc != null)
				this.usage += " &7- " + desc.value();
		}
	}

	public String getUsageMessage() {
		return "Correct usage: /" + aliasUsed + " " + usage;
	}

	public void handleException(Throwable ex) {
		if (Nexus.isDebug())
			ex.printStackTrace();
		String PREFIX = command.getPrefix();
		if (isNullOrEmpty(PREFIX))
			PREFIX = Commands.getPrefix(command);

		if (ex instanceof MissingArgumentException)
			reply(PREFIX + "&c" + getUsageMessage());
		else if (ex.getCause() != null && ex.getCause() instanceof NexusException nexusException)
			reply(new JsonBuilder(PREFIX + "&c").next(nexusException.getJson()));
		else if (ex instanceof NexusException nexusException)
			reply(new JsonBuilder(PREFIX + "&c").next(nexusException.getJson()));
		else if (ex.getCause() != null && ex.getCause() instanceof EdenException)
			reply(PREFIX + "&c" + ex.getCause().getMessage());
		else if (ex instanceof EdenException)
			reply(PREFIX + "&c" + ex.getMessage());
		else if (ex instanceof IllegalArgumentException && ex.getMessage() != null && ex.getMessage().contains("type mismatch"))
			reply(PREFIX + "&c" + getUsageMessage());
		else {
			reply("&cAn internal error occurred while attempting to execute this command");

			if (ex.getCause() != null && ex instanceof InvocationTargetException)
				ex.getCause().printStackTrace();
			else
				ex.printStackTrace();
		}
	}

}
