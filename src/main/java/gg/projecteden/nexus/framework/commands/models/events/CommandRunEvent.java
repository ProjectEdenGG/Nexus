package gg.projecteden.nexus.framework.commands.models.events;

import gg.projecteden.api.common.exceptions.EdenException;
import gg.projecteden.nexus.framework.commands.Commands;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.framework.exceptions.preconfigured.MissingArgumentException;
import gg.projecteden.nexus.utils.Debug;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@Data
@RequiredArgsConstructor
public class CommandRunEvent extends CommandEvent {
	private Method method;
	private String usage;

	public CommandRunEvent(CommandSender sender, CustomCommand command, String aliasUsed, List<String> args, List<String> originalArgs) {
		super(sender, command, aliasUsed, args, originalArgs, false);
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
		Debug.log("Handling command framework exception for " + getSender().getName(), ex);

		String PREFIX = command.getPrefix();
		if (Nullables.isNullOrEmpty(PREFIX))
			PREFIX = Commands.getPrefix(command);

		if (ex instanceof MissingArgumentException) {
			reply(PREFIX + "&c" + getUsageMessage());
			return;
		}

		if (ex.getCause() != null && ex.getCause() instanceof NexusException nexusException) {
			reply(new JsonBuilder(PREFIX + "&c").next(nexusException.getJson()));
			return;
		}

		if (ex instanceof NexusException nexusException) {
			reply(new JsonBuilder(PREFIX + "&c").next(nexusException.getJson()));
			return;
		}

		if (ex.getCause() != null && ex.getCause() instanceof EdenException edenException) {
			reply(PREFIX + "&c" + edenException.getMessage());
			return;
		}

		if (ex instanceof EdenException) {
			reply(PREFIX + "&c" + ex.getMessage());
			return;
		}

		if (ex instanceof IllegalArgumentException && ex.getMessage() != null && ex.getMessage().contains("type mismatch")) {
			reply(PREFIX + "&c" + getUsageMessage());
			return;
		}

		reply("&cAn internal error occurred while attempting to execute this command");

		if (ex.getCause() != null && ex instanceof InvocationTargetException)
			ex.getCause().printStackTrace();
		else
			ex.printStackTrace();
	}

}
