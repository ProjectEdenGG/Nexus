package me.pugabyte.bncore.framework.commands;

import com.google.common.base.Strings;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.commands.models.events.TabEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter {
	private CustomCommand customCommand;

	CommandHandler(CustomCommand customCommand) {
		this.customCommand = customCommand;
	}

	private void call(CommandEvent event) {
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		CommandEvent event = new CommandEvent(sender, customCommand, Arrays.asList(args));
		call(event);
		if (!event.isCancelled())
			customCommand.execute(event);

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		TabEvent event = new TabEvent(sender, customCommand, new ArrayList<>(Arrays.asList(args)));

		// Remove any empty args except the last one
		boolean lastIndexIsEmpty = Strings.isNullOrEmpty(event.getArgs().get(event.getArgs().size() - 1));
		event.getArgs().removeIf(Strings::isNullOrEmpty);
		if (lastIndexIsEmpty)
			event.getArgs().add("");

		call(event);
		if (!event.isCancelled())
			return customCommand.tab(event);

		return new ArrayList<>();
	}

}
