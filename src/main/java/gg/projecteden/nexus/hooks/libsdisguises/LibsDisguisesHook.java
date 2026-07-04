package gg.projecteden.nexus.hooks.libsdisguises;

import gg.projecteden.nexus.hooks.IHook;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class LibsDisguisesHook extends IHook<LibsDisguisesHook> {

	@Override
	protected @NotNull String getPluginName() {
		return "LibsDisguises";
	}

	public boolean isDisguised(Entity entity) {
		return false;
	}

	public void undisguiseToAll(Entity entity) {
		// no-op
	}

	public void setActionBarShown(Player player, boolean isShown) {
		// no-op
	}

	@Getter
	@SuppressWarnings("unused")
	public static class LibsDisguisesDisguiseEvent extends Event implements Cancellable {
		@Getter
		private static final HandlerList handlerList = new HandlerList();
		private final CommandSender commandSender;
		private final Entity entity;
		@Setter
		private boolean cancelled;

		public LibsDisguisesDisguiseEvent(CommandSender sender, Entity entity) {
			this.commandSender = sender;
			this.entity = entity;
		}

		public Entity getDisguised() {
			return this.getEntity();
		}

		public @NonNull HandlerList getHandlers() {
			return handlerList;
		}

	}

	@Getter
	public class LibsDisguisesUndisguiseEvent extends Event implements Cancellable {
		@Getter
		private static final HandlerList handlerList = new HandlerList();
		private final Entity disguised;
		private final boolean isBeingReplaced;
		private final CommandSender commandSender;
		@Setter
		private boolean isCancelled;

		public LibsDisguisesUndisguiseEvent(CommandSender sender, Entity entity, boolean beingReplaced) {
			this.commandSender = sender;
			this.disguised = entity;
			this.isBeingReplaced = beingReplaced;
		}

		public Entity getEntity() {
			return this.getDisguised();
		}

		public @NonNull HandlerList getHandlers() {
			return handlerList;
		}

	}

}
