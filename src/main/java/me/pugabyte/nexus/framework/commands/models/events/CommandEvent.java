package me.pugabyte.nexus.framework.commands.models.events;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.exceptions.NexusException;
import me.pugabyte.nexus.framework.exceptions.preconfigured.MustBeIngameException;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public abstract class CommandEvent extends Event implements Cancellable {
	@NonNull
	protected CommandSender sender;
	@NonNull
	protected CustomCommand command;
	@NonNull
	protected String aliasUsed;
	@NonNull
	protected List<String> args;
	@NonNull
	protected List<String> originalArgs;

	protected boolean cancelled = false;
	protected static final HandlerList handlers = new HandlerList();

	public void reply(String message) {
		reply(new JsonBuilder(message));
	}

	public void reply(JsonBuilder json) {
		PlayerUtils.send(sender, json);
	}

	public Player getPlayer() throws NexusException {
		if (!(sender instanceof Player))
			throw new MustBeIngameException();

		return (Player) sender;
	}

	public String getAliasUsed() {
		return aliasUsed.replace("nexus:", "");
	}

	public String getOriginalMessage() {
		return "/" + getAliasUsed() + " " + getOriginalArgsString();
	}

	public String getArgsString() {
		return String.join(" ", args);
	}

	public String getOriginalArgsString() {
		return String.join(" ", originalArgs);
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	abstract public void handleException(Throwable ex);
}
