package me.pugabyte.bncore.models.commands.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.exceptions.BNException;
import me.pugabyte.bncore.models.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.exceptions.preconfigured.MustBeCommandBlockException;
import me.pugabyte.bncore.models.exceptions.preconfigured.MustBeConsoleException;
import me.pugabyte.bncore.models.exceptions.preconfigured.MustBeIngameException;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static me.pugabyte.bncore.BNCore.*;

@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings({"SameParameterValue", "unused", "WeakerAccess"})
public abstract class CustomCommand extends TabCompleter implements ICustomCommand {
	protected CommandEvent event;
	protected final String PREFIX = BNCore.getPrefix(listLast(this.getClass().getName(), ".").replaceAll("Command", ""));

	public String getPrefix() {
		return PREFIX;
	}

	protected void send(Player player, String message) {
		player.sendMessage(colorize(message));
	}

	protected void send(int delay, Player player, String message) {
		BNCore.wait(delay, () -> player.sendMessage(colorize(message)));
	}

	protected void reply(String message) {
		event.reply(message);
	}

	public void error(String error) {
		throw new InvalidInputException(error);
	}

	protected CommandSender sender() {
		return event.getSender();
	}

	protected Player player() {
		if (!(event.getSender() instanceof Player))
			throw new MustBeIngameException();

		return (Player) event.getSender();
	}

	protected ConsoleCommandSender console() {
		if (!(event.getSender() instanceof ConsoleCommandSender))
			throw new MustBeConsoleException();

		return (ConsoleCommandSender) event.getSender();
	}

	protected CommandBlock commandBlock() {
		if (!(event.getSender() instanceof CommandBlock))
			throw new MustBeCommandBlockException();

		return (CommandBlock) event.getSender();
	}

	protected String arg(int i) {
		return arg(1, false);
	}

	protected String arg(int i, boolean rest) {
		if (event.getArgs().size() < i) return null;
		if (rest)
			return String.join(" ", event.getArgs().subList(i - 1, event.getArgs().size()));
		return event.getArgs().get(i - 1);
	}

	protected Integer intArg(int i) {
		if (event.getArgs().size() < i) return null;
		try {
			return Integer.parseInt(event.getArgs().get(i - 1));
		} catch (NumberFormatException ex) {
			throw new InvalidInputException("Argument #" + i + " is not a valid integer");
		}
	}

	protected Double doubleArg(int i) {
		if (event.getArgs().size() < i) return null;
		try {
			return Double.parseDouble(event.getArgs().get(i - 1));
		} catch (NumberFormatException ex) {
			throw new InvalidInputException("Argument #" + i + " is not a valid number");
		}
	}

	protected Boolean booleanArg(int i) {
		if (event.getArgs().size() < i) return null;
		return Boolean.parseBoolean(event.getArgs().get(i - 1));
	}

	protected OfflinePlayer playerArg(int i) {
		if (event.getArgs().size() < i) return null;
		return getPlayer(event.getArgs().get(i - 1));
	}

}


