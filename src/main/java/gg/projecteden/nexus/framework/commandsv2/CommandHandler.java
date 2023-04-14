package gg.projecteden.nexus.framework.commandsv2;

import gg.projecteden.nexus.framework.commandsv2.events.CommandRunEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMeta;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandHandler implements CommandExecutor, Listener {
	private final CustomCommandMeta commandMeta;

	CommandHandler(CustomCommandMeta commandMeta) {
		this.commandMeta = commandMeta;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
		CommandRunEvent event = new CommandRunEvent(sender, commandMeta, alias, new ArrayList<>(Arrays.asList(args)), Arrays.asList(args));
		if (event.callEvent())
			commandMeta.execute(event);

		return true;
	}

}
