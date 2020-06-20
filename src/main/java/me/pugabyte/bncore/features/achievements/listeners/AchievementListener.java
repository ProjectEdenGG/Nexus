package me.pugabyte.bncore.features.achievements.listeners;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.achievements.events.AchievementCompletedEvent;
import me.pugabyte.bncore.models.achievement.Achievement;
import me.pugabyte.bncore.models.achievement.AchievementGroup;
import me.pugabyte.bncore.models.achievement.AchievementPlayer;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AchievementListener implements Listener {

	@EventHandler
	public void onAchievementCompleted(AchievementCompletedEvent event) {
		AchievementPlayer achievementPlayer = event.getAchievementPlayer();
		Achievement achievement = event.getAchievement();

		achievementPlayer.addAchievement(achievement);
		achievementPlayer.setAchievementProgress(achievement, null);

		// Spammy...
		if (achievement.getGroup() == AchievementGroup.BIOMES) return;

		BNCore.log(achievementPlayer.getPlayer().getName() + " has completed the " + achievement.toString() + " achievement");

		Player player = achievementPlayer.getPlayer();
		if (player.isOnline()) {
			String message = StringUtils.getPrefix("Achievements") + "You have completed the &e" + achievement.toString() + " &3achievement!";
			player.sendMessage(new JsonBuilder(message).hover("&e" + achievement.getDescription()).build());
		}

	}

}
