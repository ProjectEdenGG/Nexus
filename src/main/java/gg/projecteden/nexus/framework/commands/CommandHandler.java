package gg.projecteden.nexus.framework.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.events.CommandRunEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandHandler implements CommandExecutor, Listener {
	private final CustomCommand customCommand;

	CommandHandler(CustomCommand customCommand) {
		this.customCommand = customCommand;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
		CommandRunEvent event = new CommandRunEvent(sender, customCommand, alias, new ArrayList<>(Arrays.asList(args)), Arrays.asList(args));
		if (event.callEvent())
			customCommand.execute(event);

		return true;
	}

}
