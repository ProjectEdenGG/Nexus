package gg.projecteden.nexus.features.minigames.models.events.matches.minigamers;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.DeathMessagesCommand;
import gg.projecteden.nexus.features.minigames.models.MinigameMessageType;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.models.deathmessages.DeathMessages;
import gg.projecteden.nexus.models.deathmessages.DeathMessagesService;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class MinigamerDeathEvent extends MinigamerMatchEvent implements Cancellable {
	@Nullable private final Minigamer attacker;
	@Nullable private final Event originalEvent;
	@Nullable private ComponentLike deathMessage;
	@Accessors(fluent = true)
	private boolean showDeathMessage = true;

	public MinigamerDeathEvent(@NonNull Minigamer victim) {
		super(victim);
		this.attacker = null;
		this.originalEvent = null;
	}

	public MinigamerDeathEvent(@NonNull Minigamer victim, @Nullable Minigamer attacker) {
		super(victim);
		this.attacker = attacker;
		this.originalEvent = null;
	}

	public MinigamerDeathEvent(@NonNull Minigamer victim, @Nullable Event originalEvent) {
		super(victim);
		this.attacker = null;
		this.originalEvent = originalEvent;
	}

	public MinigamerDeathEvent(@NonNull Minigamer victim, @Nullable Minigamer attacker, @Nullable Event originalEvent) {
		super(victim);
		this.attacker = attacker;
		this.originalEvent = originalEvent;
	}

	public void setDeathMessage(String deathMessage) {
		setDeathMessage(new JsonBuilder(deathMessage));
	}

	public void setDeathMessage(ComponentLike deathMessage) {
		this.deathMessage = deathMessage;
	}

	public void broadcastDeathMessage() {
		if (minigamer.getMatch() == null)
			return;
		if (!showDeathMessage)
			return;

		if (deathMessage == null) {
			boolean showTeam = minigamer.getMatch().getMechanic().showTeamOnDeath();
			String victimName = showTeam ? minigamer.getColoredName() : "&3" + minigamer.getNickname();
			if (attacker == null)
				deathMessage = new JsonBuilder(victimName + " &3died");
			else
				deathMessage = new JsonBuilder(victimName + " &3was killed by " + (showTeam ? attacker.getColoredName() : "&3" + attacker.getNickname()));
		} else {
			JsonBuilder output = new JsonBuilder();
			if (deathMessage instanceof TranslatableComponent deathMessage) {
				final DeathMessages user = new DeathMessagesService().get(minigamer);
				output.next(deathMessage.args(deathMessage.args().stream().map(arg -> DeathMessagesCommand.handleArgument(user, arg)).toList()));
			} else {
				Nexus.warn("Death message ("+deathMessage.asComponent().examinableName()+") is not translatable: " + AdventureUtils.asPlainText(deathMessage));
				output.next(deathMessage);
			}
		}

		getMatch().broadcast(deathMessage, MinigameMessageType.DEATH);
	}

	protected boolean cancelled = false;

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Getter
	private static final HandlerList handlerList = new HandlerList();

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}

}
