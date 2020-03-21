package me.pugabyte.bncore.features.chatold.alerts.models;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DiscordMessageEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private String message;
	private String permission;
	private boolean cancelled = false;

	public DiscordMessageEvent(String message, String permission) {
		this.message = message;
		this.permission = permission;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPermission() {
		return this.permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
