package me.pugabyte.bncore.framework.commands.models;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.MustBeCommandBlockException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.MustBeConsoleException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.MustBeIngameException;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@SuppressWarnings({"SameParameterValue", "unused", "WeakerAccess"})
public abstract class CustomCommand implements ICustomCommand {
	@NonNull
	@Getter
	protected CommandEvent event;
	public String PREFIX = Utils.getPrefix(Utils.listLast(this.getClass().getName(), ".").replaceAll("Command", ""));

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

	protected boolean isNullOrEmpty(String string) {
		return Strings.isNullOrEmpty(string);
	}

	protected void runCommand(String command) {
		Bukkit.dispatchCommand(sender(), command);
	}

	protected String arg(int i) {
		return arg(i, false);
	}

	protected String arg(int i, boolean rest) {
		if (event.getArgs().size() < i) return null;
		if (rest)
			return String.join(" ", event.getArgs().subList(i - 1, event.getArgs().size()));

		String result = event.getArgs().get(i - 1);
		if (Strings.isNullOrEmpty(result)) return null;
		return result;
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

	@ConverterFor({Player.class, OfflinePlayer.class})
	public Object convertToPlayer(String value) {
		if ("self".equalsIgnoreCase(value)) value = player().getUniqueId().toString();
		return Utils.getPlayer(value);
	}

	@TabCompleterFor({Player.class, OfflinePlayer.class})
	public List<String> tabCompletePlayer(String filter) {
		return Bukkit.getOnlinePlayers().stream()
				.filter(player -> player.getName().toLowerCase().startsWith(filter.toLowerCase()))
				.map(Player::getName)
				.collect(Collectors.toList());
	}

}
