package me.pugabyte.bncore.framework.commands.models;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.Fallback;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.MustBeCommandBlockException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.MustBeConsoleException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.MustBeIngameException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.NoPermissionException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.PlayerNotFoundException;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@SuppressWarnings({"SameParameterValue", "unused", "WeakerAccess", "UnusedReturnValue"})
public abstract class CustomCommand implements ICustomCommand {
	@NonNull
	@Getter
	protected CommandEvent event;
	public String PREFIX = StringUtils.replaceLast(StringUtils.getPrefix(StringUtils.listLast(this.getClass().getName(), ".")), "Command", "");

	public String getPrefix() {
		return PREFIX;
	}

	public String getAliasUsed() {
		return event.getAliasUsed();
	}

	protected void send(CommandSender sender, String message) {
		if (isConsole(sender) || isPlayer(sender))
			sender.sendMessage(StringUtils.colorize(message));
		else if (isOfflinePlayer(sender))
			if (((OfflinePlayer) sender).isOnline())
				((OfflinePlayer) sender).getPlayer().sendMessage(StringUtils.colorize(message));
	}

	protected void send(Player player, int delay, String message) {
		Tasks.wait(delay, () -> player.sendMessage(StringUtils.colorize(message)));
	}

	protected void send() {
		send("");
	}

	protected void send(String message) {
		event.reply(message);
	}

	protected void send(JsonBuilder builder) {
		sender().spigot().sendMessage(builder.build());
	}

	protected void send(Player player, JsonBuilder builder) {
		builder.send(player);
	}

	protected void send(int delay, String message) {
		Tasks.wait(delay, () -> event.reply(message));
	}

	protected void line() {
		line(1);
	}

	protected void line(int count) {
		for (int i = 0; i < count; i++) {
			send("");
		}
	}

	protected JsonBuilder json() {
		return json("");
	}

	protected JsonBuilder json(String message) {
		return new JsonBuilder(message);
	}

	public void error(String error) {
		throw new InvalidInputException(error);
	}

	public void error(Player player, String error) {
		player.sendMessage(StringUtils.colorize("&c" + error));
	}

	public void showUsage() {
		throw new InvalidInputException(event.getUsageMessage());
	}

	protected CommandSender sender() {
		return event.getSender();
	}

	protected Player player() {
		if (!isPlayer())
			throw new MustBeIngameException();

		return (Player) event.getSender();
	}

	protected ConsoleCommandSender console() {
		if (!isConsole())
			throw new MustBeConsoleException();

		return (ConsoleCommandSender) event.getSender();
	}

	protected BlockCommandSender commandBlock() {
		if (!isCommandBlock())
			throw new MustBeCommandBlockException();

		return (BlockCommandSender) event.getSender();
	}

	protected boolean isPlayer() {
		return isPlayer(sender());
	}

	private boolean isPlayer(Object object) {
		return object instanceof Player;
	}

	protected boolean isOfflinePlayer() {
		return isOfflinePlayer(sender());
	}

	private boolean isOfflinePlayer(Object object) {
		return object instanceof OfflinePlayer;
	}

	protected boolean isConsole() {
		return isConsole(sender());
	}

	private boolean isConsole(Object object) {
		return object instanceof ConsoleCommandSender;
	}

	protected boolean isCommandBlock() {
		return isCommandBlock(sender());
	}

	private boolean isCommandBlock(Object object) {
		return object instanceof BlockCommandSender;
	}

	protected boolean isSelf(OfflinePlayer player) {
		return player.equals(player());
	}

	protected boolean isSelf(Player player) {
		return player.equals(player());
	}

	protected boolean isNullOrEmpty(String string) {
		return Strings.isNullOrEmpty(string);
	}

	protected void runCommand(String command) {
		runCommand(sender(), command);
	}

	protected void runCommand(CommandSender sender, String command) {
		Bukkit.dispatchCommand(sender, command);
	}

	protected void runCommandAsOp(String command) {
		runCommandAsOp(sender(), command);
	}

	protected void runCommandAsOp(CommandSender sender, String command) {
		Utils.runCommandAsOp(sender, command);
	}

	protected void runCommandAsConsole(String command) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
	}

	protected void checkPermission(String permission) {
		if (!sender().hasPermission(permission))
			throw new NoPermissionException();
	}

	protected String argsString() {
		return argsString(args());
	}

	protected String argsString(List<String> args) {
		if (args() == null || args().size() == 0) return "";
		return String.join(" ", args());
	}

	protected List<String> args() {
		return event.getArgs();
	}

	protected void setArgs(List<String> args) {
		event.setArgs(args);
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

	protected boolean isIntArg(int i) {
		if (event.getArgs().size() < i) return false;
		try {
			Integer.parseInt(arg(i));
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	protected Integer intArg(int i) {
		if (event.getArgs().size() < i) return null;
		try {
			return Integer.parseInt(arg(i));
		} catch (NumberFormatException ex) {
			throw new InvalidInputException("Argument #" + i + " is not a valid integer");
		}
	}

	protected Double doubleArg(int i) {
		if (event.getArgs().size() < i) return null;
		try {
			return Double.parseDouble(arg(i));
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

	protected boolean isPlayerArg(int i) {
		if (event.getArgs().size() < i) return false;
		try {
			Utils.getPlayer(arg(i));
			return true;
		} catch (PlayerNotFoundException ex) {
			return false;
		}
	}

	protected OfflinePlayer playerArg(int i) {
		if (event.getArgs().size() < i) return null;
		return Utils.getPlayer(arg(i));
	}

	protected void fallback() {
		Fallback fallback = getClass().getAnnotation(Fallback.class);
		if (fallback != null)
			Bukkit.dispatchCommand(sender(), fallback.value() + ":" + event.getAliasUsed() + " " + event.getArgsString());
		else
			throw new InvalidInputException("Nothing to fallback to");
	}

	@ConverterFor(OfflinePlayer.class)
	public OfflinePlayer convertToOfflinePlayer(String value) {
		if ("self".equalsIgnoreCase(value)) value = player().getUniqueId().toString();
		return Utils.getPlayer(value);
	}

	@ConverterFor(Player.class)
	public Player convertToPlayer(String value) {
		OfflinePlayer offlinePlayer = convertToOfflinePlayer(value);
		if (!offlinePlayer.isOnline())
			throw new PlayerNotOnlineException(offlinePlayer);
		if (isPlayer() && !Utils.canSee(player(), offlinePlayer.getPlayer()))
			throw new PlayerNotOnlineException(offlinePlayer);

		return offlinePlayer.getPlayer();
	}

	@TabCompleterFor({Player.class, OfflinePlayer.class})
	public List<String> tabCompletePlayer(String filter) {
		return Bukkit.getOnlinePlayers().stream()
				.filter(player -> Utils.canSee(player(), player))
				.filter(player -> player.getName().toLowerCase().startsWith(filter.toLowerCase()))
				.map(Player::getName)
				.collect(Collectors.toList());
	}

	@ConverterFor(World.class)
	World convertToWorld(String value) {
		if ("current".equalsIgnoreCase(value))
			return player().getWorld();
		World world = Bukkit.getWorld(value);
		if (world == null)
			throw new InvalidInputException("World from " + value + " not found");
		return world;
	}

	@TabCompleterFor(World.class)
	List<String> tabCompleteWorld(String filter) {
		return Bukkit.getWorlds().stream()
				.filter(world -> world.getName().toLowerCase().startsWith(filter.toLowerCase()))
				.map(World::getName)
				.collect(Collectors.toList());
	}

	@ConverterFor(Material.class)
	Material convertToMaterial(String value) {
		Material material = Material.matchMaterial(value);
		if (material == null)
			throw new InvalidInputException("Material from " + value + " not found");
		return material;
	}

}
