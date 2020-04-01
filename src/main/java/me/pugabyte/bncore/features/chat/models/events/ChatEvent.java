package me.pugabyte.bncore.features.chat.models.events;

import me.pugabyte.bncore.features.chat.ChatManager;
import me.pugabyte.bncore.features.chat.models.Channel;
import me.pugabyte.bncore.features.chat.models.Chatter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Set;

public abstract class ChatEvent extends Event implements Cancellable {

	public abstract Chatter getChatter();

	public abstract String getOrigin();

	public abstract String getMessage();

	public abstract void setMessage(String message);

	public abstract Channel getChannel();

	public abstract Set<Chatter> getRecipients();

	public boolean wasSentTo(Player player) {
		return wasSentTo(ChatManager.getChatter(player));
	}

	public boolean wasSentTo(Chatter chatter) {
		return getRecipients().contains(chatter);
	}

	//<editor-fold desc="Boilerplate Bukkit">
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
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
