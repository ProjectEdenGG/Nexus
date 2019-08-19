package me.pugabyte.bncore.models.commands;

import me.pugabyte.bncore.models.commands.models.CustomCommand;
import me.pugabyte.bncore.models.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.commands.models.events.TabEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

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
		TabEvent event = new TabEvent(sender, customCommand, Arrays.asList(args));
		call(event);
		if (!event.isCancelled())
			return customCommand.tab(event);

		return null;
	}

}
