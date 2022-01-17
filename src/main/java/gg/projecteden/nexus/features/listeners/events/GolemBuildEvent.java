package gg.projecteden.nexus.features.listeners.events;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Golem;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class GolemBuildEvent extends PlayerEvent {
	@NonNull
	@Getter
	final Golem entity;

	public GolemBuildEvent(@NotNull Player who, @NonNull Golem entity) {
		super(who);
		this.entity = entity;
	}

	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static class IronGolemBuildEvent extends GolemBuildEvent {

		public IronGolemBuildEvent(@NotNull Player who, @NonNull IronGolem entity) {
			super(who, entity);
		}

	}

	public static class SnowGolemBuildEvent extends GolemBuildEvent {

		public SnowGolemBuildEvent(@NotNull Player who, @NonNull Snowman entity) {
			super(who, entity);
		}

	}

}
