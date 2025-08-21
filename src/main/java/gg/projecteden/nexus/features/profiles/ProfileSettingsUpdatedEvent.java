package gg.projecteden.nexus.features.profiles;

import gg.projecteden.nexus.models.profile.ProfileUser;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class ProfileSettingsUpdatedEvent extends PlayerEvent {

	public ProfileSettingsUpdatedEvent(@NotNull ProfileUser user) {
		super(user.getOnlinePlayer());
	}

	@Getter
	private static final HandlerList handlerList = new HandlerList();

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}
}
