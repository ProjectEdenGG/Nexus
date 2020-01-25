package me.pugabyte.bncore.framework.commands.models.events;

import lombok.Data;
import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.exceptions.BNException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.MustBeIngameException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static me.pugabyte.bncore.utils.Utils.colorize;

@Data
public class CommandEvent extends Event implements Cancellable {
	@NonNull
	private CommandSender sender;
	@NonNull
	private CustomCommand command;
	@NonNull
	private String aliasUsed;
	@NonNull
	private List<String> args;
	private String usage;
	private boolean cancelled = false;
	private HandlerList handlers = new HandlerList();

	public void reply(String message) {
		sender.sendMessage(colorize(message));
	}

	public Player getPlayer() throws BNException {
		if (!(sender instanceof Player))
			throw new MustBeIngameException();

		return (Player) sender;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public String getAliasUsed() {
		return aliasUsed.replace("bncore:", "");
	}

	public void setUsage(Method method) {
		Path annotation = method.getAnnotation(Path.class);
		if (annotation != null)
			this.usage = annotation.value();
	}

	public void handleException(Exception ex) {
		if (ex.getCause() != null && ex.getCause() instanceof BNException)
			reply(command.getPrefix() + "&c" + ex.getCause().getMessage());
		else if (ex instanceof BNException)
			reply(command.getPrefix() + "&c" + ex.getMessage());
		else if (ex instanceof IllegalArgumentException && ex.getMessage() != null && ex.getMessage().contains("type mismatch"))
			reply(command.getPrefix() + "&cIncorrect usage");
		else {
			reply("&cAn internal error occurred while attempting to execute this command");

			if (ex.getCause() != null && ex instanceof InvocationTargetException)
				ex.getCause().printStackTrace();
			else
				ex.printStackTrace();
		}
	}

}
