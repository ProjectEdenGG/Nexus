package gg.projecteden.nexus.features.mcmmo.mastery;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.util.player.UserManager;
import gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUserService;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class McMMOPrestigeListener implements Listener {

	public static long XP_TO_100;
	public static long XP_TO_MASTERY_REWARD;
	private static final McMMOPrestigeUserService SERVICE = new McMMOPrestigeUserService();

	static {
		for (int level = 0; level < 100; level++)
			XP_TO_100 += ExperienceAPI.getXpNeededToLevel(level);
		XP_TO_MASTERY_REWARD = (long) (XP_TO_100 / 2d);

		double xp = XP_TO_MASTERY_REWARD / 500_000d;
		XP_TO_MASTERY_REWARD = Math.round(xp) * 500_000L;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Tasks.wait(10, () -> reduce(event.getPlayer(), 10));
	}

	private void reduce(Player player, int retries) {
		McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
		if (mcMMOPlayer == null) {
			if (retries > 0)
				Tasks.wait(10, () -> reduce(player, retries - 1));
			return;
		}

		for (PrimarySkillType skill : PrimarySkillType.values()) {
			if (skill.isChildSkill())
				continue;

			int level = mcMMOPlayer.getSkillLevel(skill);
			if (level < 100)
				continue;

			float progress = mcMMOPlayer.getSkillXpLevelRaw(skill);
			if (level == 100 && progress == 0)
				continue;

			long xpFromLevels = 0;
			for (int fromLevel = 100; fromLevel < level; fromLevel++)
				xpFromLevels += ExperienceAPI.getXpNeededToLevel(fromLevel);

			double xp = xpFromLevels + progress;
			SERVICE.edit(player, user -> user.awardExperience(skill, xp));

			mcMMOPlayer.modifySkill(skill, 100);
			mcMMOPlayer.setSkillXpLevel(skill, 0f);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onGainXp(McMMOPlayerXpGainEvent event) {
		Player player = event.getPlayer();
		PrimarySkillType skill = event.getSkill();
		McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

		if (mcMMOPlayer == null) return;
		if (skill.isChildSkill()) return;
		if (mcMMOPlayer.getSkillLevel(skill) < 100) return;

		float xp = (long) event.getRawXpGained();
		event.setCancelled(true);

		SERVICE.edit(player, user -> user.awardExperience(skill, xp));
	}

}
