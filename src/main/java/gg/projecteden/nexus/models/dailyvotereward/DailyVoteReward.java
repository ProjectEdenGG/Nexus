package gg.projecteden.nexus.models.dailyvotereward;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.votes.DailyVoteRewardsCommand.VoteStreakReward;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity(value = "daily_vote_reward", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class DailyVoteReward implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private DailyVoteStreak currentStreak;
	private List<DailyVoteStreak> pastStreaks = new ArrayList<>();

	public DailyVoteStreak getCurrentStreak() {
		if (currentStreak == null)
			currentStreak = new DailyVoteStreak(uuid);
		return currentStreak;
	}

	public void endStreak() {
		currentStreak.end();
		pastStreaks.add(currentStreak);
		currentStreak = new DailyVoteStreak(uuid);
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@RequiredArgsConstructor
	public static class DailyVoteStreak implements PlayerOwnedObject {
		@NonNull
		private UUID uuid;
		private int streak;
		private boolean earnedToday;
		@Builder.Default
		private LocalDate start = LocalDate.now();
		private LocalDate end;

		public void incrementStreak() {
			++streak;
			Nexus.log("[VoteStreak] Increasing streak for " + getNickname() + " to " + streak);
			earnedToday = true;

			sendMessage(StringUtils.getPrefix("DailyVoteRewards") + "Your streak has &eincreased&3!");

			if (streak % 30 == 0)
				VoteStreakReward.DAY_30.getOnAchieve().accept(uuid);
			else
				for (VoteStreakReward reward : VoteStreakReward.values())
					if (reward.getDay() == streak % 30)
						reward.getOnAchieve().accept(uuid);

		}

		public void end() {
			end = LocalDate.now();
		}

	}

}
