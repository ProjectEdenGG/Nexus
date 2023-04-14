package gg.projecteden.nexus.framework.commandsv2.events;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMeta;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMetaInstance;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMetaInstance.PathMetaInstance.ArgumentMetaInstance;
import gg.projecteden.nexus.framework.exceptions.preconfigured.NoPermissionException;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandTabEvent extends CommandEvent {

	public CommandTabEvent(
		CommandSender sender,
		CustomCommandMeta commandMeta,
		String aliasUsed,
		List<String> args,
		List<String> originalArgs
	) {
		super(sender, commandMeta, aliasUsed, args, originalArgs, true);
	}

	@Override
	public void handleException(Throwable ex) {
		if (ex instanceof NoPermissionException)
			return;
		ex.printStackTrace();
	}

}
