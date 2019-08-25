package me.pugabyte.bncore.framework.commands.models.events;

import lombok.Data;
import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.exceptions.BNException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.MustBeIngameException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

import static me.pugabyte.bncore.BNCore.colorize;
import static me.pugabyte.bncore.BNCore.listLast;

@Data
public class CommandEvent extends Event implements Cancellable {
	@NonNull
	private CommandSender sender;
	@NonNull
	private CustomCommand command;
	@NonNull
	private List<String> args;
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

	public void handleException(Exception ex) {
		if (ex instanceof BNException)
			reply(command.getPrefix() + "&c" + ex.getMessage());
		else if (ex.getCause() instanceof BNException)
			reply(command.getPrefix() + "&c" + ex.getCause().getMessage());
		else {
			reply("&cAn internal error occurred while attempting to execute this command: "
					+ listLast(ex.getCause().getClass().getName(), ".") + ": " + ex.getMessage());
			ex.printStackTrace();
		}
	}

}
