package gg.projecteden.nexus.features.achievements.listeners;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.achievements.events.AchievementCompletedEvent;
import gg.projecteden.nexus.models.achievement.Achievement;
import gg.projecteden.nexus.models.achievement.AchievementGroup;
import gg.projecteden.nexus.models.achievement.AchievementPlayer;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@Disabled
public class AchievementListener implements Listener {

	@EventHandler
	public void onAchievementCompleted(AchievementCompletedEvent event) {
		AchievementPlayer achievementPlayer = event.getAchievementPlayer();
		Achievement achievement = event.getAchievement();

		achievementPlayer.addAchievement(achievement);
		achievementPlayer.setAchievementProgress(achievement, null);

		// Spammy...
		if (achievement.getGroup() == AchievementGroup.BIOMES) return;

		Nexus.log(achievementPlayer.getOnlinePlayer().getName() + " has completed the " + achievement.toString() + " achievement");

		Player player = achievementPlayer.getOnlinePlayer();
		if (player.isOnline()) {
			String message = StringUtils.getPrefix("Achievements") + "You have completed the &e" + achievement.toString() + " &3achievement!";
			PlayerUtils.send(player, new JsonBuilder(message).hover("&e" + achievement.getDescription()));
		}
	}

}
