package gg.projecteden.nexus.features.chat.events;

import gg.projecteden.nexus.models.chat.Channel;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.ChatterService;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public abstract class ChatEvent extends Event implements Cancellable {

	public ChatEvent() {
		super(true);
	}

	public abstract Chatter getChatter();

	public abstract String getOrigin();

	public abstract String getOriginalMessage();

	public abstract String getMessage();

	public abstract void setMessage(String message);

	public abstract Channel getChannel();

	public abstract Set<Chatter> getRecipients();

	public boolean wasSentTo(Player player) {
		return wasSentTo(new ChatterService().get(player));
	}

	public boolean wasSentTo(Chatter chatter) {
		return getRecipients().contains(chatter);
	}

	public void respond(String response) {
		getRecipients().forEach(chatter -> chatter.sendMessage(response));
	}

	public boolean wasChanged() {
		return !StringUtils.stripColor(getMessage()).equalsIgnoreCase(StringUtils.stripColor(getOriginalMessage()));
	}

	public abstract boolean isBad();

	public abstract void setBad(boolean wasBad);

	public abstract boolean isFiltered();

	public abstract void setFiltered(boolean wasFiltered);

	//<editor-fold desc="Boilerplate Bukkit">
	@Getter
	private static final HandlerList handlerList = new HandlerList();
	private boolean cancelled = false;

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	//</editor-fold>

}
