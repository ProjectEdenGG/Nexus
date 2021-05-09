package me.pugabyte.nexus.framework.commands.models.events;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.exceptions.preconfigured.NoPermissionException;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandTabEvent extends CommandEvent {

	public CommandTabEvent(CommandSender sender, CustomCommand command, String aliasUsed, List<String> args, List<String> originalArgs) {
		super(sender, command, aliasUsed, args, originalArgs);
	}

	@Override
	public void handleException(Throwable ex) {
		if (ex instanceof NoPermissionException)
			return;
		ex.printStackTrace();
	}

}
