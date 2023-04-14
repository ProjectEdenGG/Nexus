package gg.projecteden.nexus.framework.commandsv2.events;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.models.PathParser;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMeta;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMeta.PathMeta;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMeta.PathMeta.ArgumentMeta;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMeta.PathMeta.LiteralArgumentMeta;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMeta.PathMeta.VariableArgumentMeta;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMetaInstance;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMetaInstance.PathMetaInstance;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMetaInstance.PathMetaInstance.ArgumentMetaInstance;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.framework.exceptions.preconfigured.MustBeIngameException;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public abstract class CommandEvent extends Event implements Cancellable {
	@NonNull
	protected CommandSender sender;
	@NonNull
	protected CustomCommandMetaInstance commandMetaInstance;
	@NonNull
	protected CustomCommandMetaInstance.PathMetaInstance pathMetaInstance;
	@NonNull
	protected String aliasUsed;
	@NonNull
	protected List<ArgumentMetaInstance> args;
	@NonNull
	protected List<ArgumentMetaInstance> originalArgs;
	protected boolean async;

	public CommandEvent(
		@NonNull CommandSender sender,
		@NonNull CustomCommandMeta commandMeta,
		@NonNull String aliasUsed,
		@NonNull List<String> args,
		@NonNull List<String> originalArgs,
		boolean async
	) {
		super(async);
		this.sender = sender;
		this.commandMetaInstance = new CustomCommandMetaInstance(commandMeta, this);

		final PathMeta pathMeta = PathParser.match(commandMeta, args);
		final List<ArgumentMetaInstance> argumentMetaInstances = getArgumentMetaInstances(args, pathMeta);

		this.pathMetaInstance = commandMetaInstance.new PathMetaInstance(pathMeta, argumentMetaInstances);
		this.aliasUsed = aliasUsed;
		this.args = argumentMetaInstances;
		this.originalArgs = Collections.unmodifiableList(argumentMetaInstances);
	}

	@NotNull
	private List<ArgumentMetaInstance> getArgumentMetaInstances(List<String> args, PathMeta pathMeta) {
		return new ArrayList<>() {{
			final Iterator<ArgumentMeta> metaIterator = pathMeta.getArguments().iterator();
			final Iterator<String> inputIterator = args.iterator();
			while (inputIterator.hasNext()) {
				String arg = inputIterator.next();
				if (metaIterator.hasNext()) {
					var argumentMeta = metaIterator.next();
					if (argumentMeta instanceof LiteralArgumentMeta literalArgumentMeta) {
						add(pathMetaInstance.new LiteralArgumentMetaInstance(literalArgumentMeta, arg));
					} else if (argumentMeta instanceof VariableArgumentMeta variableArgumentMeta) {
						// TODO Switches
						if (variableArgumentMeta.isVararg()) {
							while (inputIterator.hasNext()) {
								arg += " " + inputIterator.next();
							}
						}

						add(pathMetaInstance.new VariableArgumentMetaInstance(variableArgumentMeta, arg));
					}
				}
			}
		}};
	}

	protected boolean cancelled = false;
	protected static final HandlerList handlers = new HandlerList();

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
		return args.stream().map(ArgumentMetaInstance::getInput).collect(Collectors.joining(" "));
	}

	public String getOriginalArgsString() {
		return args.stream().map(ArgumentMetaInstance::getOriginalInput).collect(Collectors.joining(" "));
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
