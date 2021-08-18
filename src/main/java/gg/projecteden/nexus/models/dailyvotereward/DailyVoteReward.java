package gg.projecteden.nexus.models.dailyvotereward;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.votes.DailyVoteRewardsCommand.VoteStreakReward;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.mail.Mailer.Mail;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
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
	@NoArgsConstructor
	@RequiredArgsConstructor
	public static class DailyVoteStreak implements PlayerOwnedObject {
		@NonNull
		private UUID uuid;
		private int streak;
		private boolean earnedToday;
		private LocalDate start = LocalDate.now();
		private LocalDate end;

		public void incrementStreak() {
			++streak;
			Nexus.log("[VoteStreak] Increasing streak for " + getNickname() + " to " + streak);
			earnedToday = true;

			sendMessage(StringUtils.getPrefix("DailyVoteRewards") + "Your streak has &eincreased&3!");

			for (VoteStreakReward reward : VoteStreakReward.values())
				if (reward.getDay() == streak % 30)
					Mail.fromServer(uuid, WorldGroup.SURVIVAL, "Vote Streak Reward (Day #" + streak + ")", reward.getKeys()).send();
		}

		public void end() {
			end = LocalDate.now();
		}

	}

}
