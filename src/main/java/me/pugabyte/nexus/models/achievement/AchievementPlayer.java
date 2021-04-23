package me.pugabyte.nexus.models.achievement;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.Data;
import lombok.NonNull;
import me.pugabyte.nexus.models.PlayerOwnedObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@Entity("achievement_player")
public class AchievementPlayer implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Set<Achievement> achievements = new HashSet<>();
	private Map<Achievement, Object> achievementProgress = new HashMap<>();

	public void addAchievement(Achievement achievement) {
		achievements.add(achievement);
	}

	public boolean hasAchievement(Achievement achievement) {
		return achievements.contains(achievement);
	}

	public Object getAchievementProgress(Achievement achievement) {
		return achievementProgress.get(achievement);
	}

	public void setAchievementProgress(Achievement achievement, Object progress) {
		if (progress == null)
			achievementProgress.remove(achievement);
		else
			achievementProgress.put(achievement, progress);
	}

}
