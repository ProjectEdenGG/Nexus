package gg.projecteden.nexus.features.achievements.events;

import gg.projecteden.nexus.models.achievement.Achievement;
import gg.projecteden.nexus.models.achievement.AchievementPlayer;
import lombok.Data;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Data
public class AchievementCompletedEvent extends Event {
	@Getter
	private static final HandlerList handlerList = new HandlerList();
	private AchievementPlayer achievementPlayer;
	private Achievement achievement;

	public AchievementCompletedEvent(AchievementPlayer achievementPlayer, Achievement achievement) {
		this.achievementPlayer = achievementPlayer;
		this.achievement = achievement;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}

}
