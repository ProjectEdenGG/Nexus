package gg.projecteden.nexus.framework.commands.models.events;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.framework.exceptions.preconfigured.MustBeIngameException;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Data
@NoArgsConstructor
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
	protected boolean async;

	public CommandEvent(@NonNull CommandSender sender, @NonNull CustomCommand command, @NonNull String aliasUsed,
						@NonNull List<String> args, @NonNull List<String> originalArgs, boolean async) {
		super(async);
		this.sender = sender;
		this.command = command;
		this.aliasUsed = aliasUsed;
		this.args = args;
		this.originalArgs = originalArgs;
	}

	protected boolean cancelled = false;
	@Getter
	protected static final HandlerList handlerList = new HandlerList();

	public void reply(String message) {
		reply(new JsonBuilder(message));
	}

	public void reply(ComponentLike component) {
		PlayerUtils.send(sender, component);
	}

	public Player getPlayer() throws NexusException {
		if (!(sender instanceof Player player))
			throw new MustBeIngameException();

		return player;
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
		return handlerList;
	}

	abstract public void handleException(Throwable ex);
}
