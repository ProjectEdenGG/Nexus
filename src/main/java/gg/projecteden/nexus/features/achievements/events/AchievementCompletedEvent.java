package gg.projecteden.nexus.features.achievements.events;

import gg.projecteden.nexus.models.achievement.Achievement;
import gg.projecteden.nexus.models.achievement.AchievementPlayer;
import lombok.Data;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Data
public class AchievementCompletedEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private AchievementPlayer achievementPlayer;
	private Achievement achievement;

	public AchievementCompletedEvent(AchievementPlayer achievementPlayer, Achievement achievement) {
		this.achievementPlayer = achievementPlayer;
		this.achievement = achievement;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

}
