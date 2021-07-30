package gg.projecteden.nexus.models.dailyreward;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.LocalDateConverter;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Name;
import gg.projecteden.nexus.utils.PlayerUtils;
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

import static gg.projecteden.nexus.utils.StringUtils.getPrefix;

@Data
@Builder
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
			currentStreak = new DailyStreak();
		return currentStreak;
	}

	public void endStreak() {
		currentStreak.end();
		pastStreaks.add(currentStreak);
		currentStreak = new DailyStreak();
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
		private List<Integer> claimed = new ArrayList<>();
		private LocalDate start = LocalDate.now();
		private LocalDate end;

		public void increaseStreak() {
			Nexus.log("[DailyRewards] Increasing streak for " + Name.of(getOfflinePlayer()));
			earnedToday = true;
			++streak;
			PlayerUtils.send(getOfflinePlayer(), new JsonBuilder()
				.next(getPrefix("DailyRewards") + "Your streak has &eincreased&3! " + "Use &c/dailyrewards &3to claim your reward")
				.command("/dr"));
		}

		public boolean hasClaimed(int day) {
			return claimed != null && claimed.contains(day);
		}

		public void claim(int day){
			if (claimed == null)
				claimed = new ArrayList<>();
			claimed.add(day);
		}

		public void unclaim(Integer day) {
			if (claimed != null)
				claimed.remove(day);
		}

		public void end() {
			end = LocalDate.now();
		}

	}

}
