package gg.projecteden.nexus.models.dailyreward;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.LocalDateConverter;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

@Data
@Entity(value = "daily_reward_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class DailyRewardUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private DailyStreak currentStreak;
	private List<DailyStreak> pastStreaks = new ArrayList<>();

	public DailyStreak getCurrentStreak() {
		if (currentStreak == null)
			currentStreak = new DailyStreak(uuid);
		return currentStreak;
	}

	public void endStreak() {
		currentStreak.end();
		pastStreaks.add(currentStreak);
		currentStreak = new DailyStreak(uuid);
	}

	@Data
	@NoArgsConstructor
	@RequiredArgsConstructor
	@Converters({UUIDConverter.class, LocalDateConverter.class})

	public static class DailyStreak implements PlayerOwnedObject {
		@NonNull
		private UUID uuid;
		private int streak;
		private boolean earnedToday;
		private Set<Integer> claimed = new HashSet<>();
		private LocalDate start = LocalDate.now();
		private LocalDate end;

		public void increaseStreak() {
			Nexus.log("[DailyRewards] Increasing streak for " + Nickname.of(uuid));
			earnedToday = true;
			++streak;
			sendMessage(new JsonBuilder()
				.next(StringUtils.getPrefix("DailyRewards") + "Your streak has &eincreased&3! " + "Use &c/dailyrewards &3to claim your reward")
				.command("/dr"));
		}

		public boolean hasClaimed(int day) {
			return claimed.contains(day);
		}

		public boolean canClaim(int day) {
			return streak >= day;
		}

		public void claim(int day){
			claimed.add(day);
		}

		public void unclaim(Integer day) {
			claimed.remove(day);
		}

		public void end() {
			end = LocalDate.now();
		}

	}

}
