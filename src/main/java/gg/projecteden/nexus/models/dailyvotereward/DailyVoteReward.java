package gg.projecteden.nexus.models.dailyvotereward;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.votes.DailyVoteRewardsCommand.VoteStreakReward;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.mail.Mailer.Mail;
import gg.projecteden.nexus.utils.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity(value = "daily_vote_rewards", noClassnameStored = true)
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
			currentStreak = new DailyVoteStreak();
		return currentStreak;
	}

	@Data
	@NoArgsConstructor
	public static class DailyVoteStreak implements PlayerOwnedObject {
		@NonNull
		private UUID uuid;
		private int streak;
		private boolean earnedToday;

		public void incrementStreak() {
			++streak;
			earnedToday = true;

			for (VoteStreakReward reward : VoteStreakReward.values())
				if (reward.getDay() == streak % 30)
					Mail.fromServer(uuid, WorldGroup.SURVIVAL, "Vote Streak Reward (Day #" + streak + ")", reward.getKeys());
		}

	}

}
