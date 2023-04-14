package gg.projecteden.nexus.framework.commandsv2.events;

import gg.projecteden.api.common.exceptions.EdenException;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commandsv2.Commands;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMeta;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMetaInstance;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMetaInstance.PathMetaInstance.ArgumentMetaInstance;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.framework.exceptions.preconfigured.MissingArgumentException;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;

@Data
@RequiredArgsConstructor
public class CommandRunEvent extends CommandEvent {
	private CustomCommandMetaInstance.PathMetaInstance pathMeta;
	private String usage;

	public CommandRunEvent(
		CommandSender sender,
		CustomCommandMeta commandMeta,
		String aliasUsed,
		List<String> args,
		List<String> originalArgs
	) {
		super(sender, commandMeta, aliasUsed, args, originalArgs, false);
	}

	public String getUsageMessage() {
		return "Correct usage: /" + aliasUsed + " " + pathMeta.getUsage();
	}

	public void handleException(Throwable ex) {
		if (Nexus.isDebug()) {
			Nexus.debug("Handling command framework exception for " + getSender().getName());
			ex.printStackTrace();
		}

		final CustomCommandMeta commandMeta = commandMetaInstance.getCommandMeta();
		String PREFIX = commandMeta.getInstance().getPrefix();
		if (isNullOrEmpty(PREFIX))
			PREFIX = Commands.getPrefix(commandMeta.getClazz());

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
