package gg.projecteden.nexus.framework.commands;

import com.google.common.base.Strings;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.events.CommandRunEvent;
import gg.projecteden.nexus.framework.commands.models.events.CommandTabEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter, Listener {
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

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
		CommandTabEvent event = new CommandTabEvent(sender, customCommand, alias, new ArrayList<>(Arrays.asList(args)), Arrays.asList(args));

		// Remove any empty args except the last one
		boolean lastIndexIsEmpty = Strings.isNullOrEmpty(event.getArgs().get(event.getArgs().size() - 1));
		event.getArgs().removeIf(Strings::isNullOrEmpty);
		if (lastIndexIsEmpty)
			event.getArgs().add("");

		if (event.callEvent())
			return customCommand.tabComplete(event);

		return new ArrayList<>();
	}

}
