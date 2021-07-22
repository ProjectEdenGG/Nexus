package gg.projecteden.nexus.models.dailyreward;

import com.dieselpoint.norm.serialize.DbSerializer;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.persistence.serializer.mysql.IntegerListSerializer;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Name;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.utils.StringUtils.getPrefix;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "daily_reward")
public class DailyReward {
	@Id
	@GeneratedValue
	private int dailyRewardId;
	@NonNull
	private String uuid;
	private int streak;
	private boolean earnedToday;
	private boolean active = true;
	@DbSerializer(IntegerListSerializer.class)
	private List<Integer> claimed;

	public OfflinePlayer getOfflinePlayer() {
		return PlayerUtils.getPlayer(uuid);
	}

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

	public void reset() {
		streak = 0;
		claimed = null;
		earnedToday = true;
	}



}
