package me.pugabyte.bncore.framework.commands.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.Utils;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.MustBeCommandBlockException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.MustBeConsoleException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.MustBeIngameException;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@SuppressWarnings({"SameParameterValue", "unused", "WeakerAccess"})
public abstract class CustomCommand extends TabCompleter implements ICustomCommand {
	@NonNull
	protected CommandEvent event;
	protected String PREFIX = Utils.getPrefix(Utils.listLast(this.getClass().getName(), ".").replaceAll("Command", ""));

	public String getPrefix() {
		return PREFIX;
	}

	protected void send(Player player, String message) {
		player.sendMessage(Utils.colorize(message));
	}

	protected void send(Player player, String message, int delay) {
		Utils.wait(delay, () -> player.sendMessage(Utils.colorize(message)));
	}

	protected void reply(String message) {
		event.reply(message);
	}

	protected void newline() {
		reply("");
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

	public Object convert(String value, Class<?> type) {
		if (Player.class == type || OfflinePlayer.class == type) {
			if ("self".equalsIgnoreCase(value)) value = ((Player) event.getSender()).getUniqueId().toString();
			return Utils.getPlayer(value);
		}
		if (Boolean.class == type || Boolean.TYPE == type) {
			if (Arrays.asList("enable", "on", "yes", "1").contains(value)) value = "true";
			return Boolean.parseBoolean(value);
		}
		if (Integer.class == type || Integer.TYPE == type) return Integer.parseInt(value);
		if (Double.class == type || Double.TYPE == type) return Double.parseDouble(value);
		if (Float.class == type || Float.TYPE == type) return Float.parseFloat(value);
		if (Short.class == type || Short.TYPE == type) return Short.parseShort(value);
		if (Long.class == type || Long.TYPE == type) return Long.parseLong(value);
		if (Byte.class == type || Byte.TYPE == type) return Byte.parseByte(value);
		return value;
	}

	protected String arg(int i) {
		return arg(i, false);
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
		String value = arg(i);
		if (Arrays.asList("enable", "on", "yes", "1").contains(value)) value = "true";
		return Boolean.parseBoolean(value);
	}

	protected OfflinePlayer playerArg(int i) {
		if (event.getArgs().size() < i) return null;
		return Utils.getPlayer(event.getArgs().get(i - 1));
	}

}


