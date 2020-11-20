package me.pugabyte.nexus.models.dailyreward;

import com.dieselpoint.norm.serialize.DbSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.framework.persistence.serializer.mysql.IntegerListSerializer;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.OfflinePlayer;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

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

	public OfflinePlayer getPlayer() {
		return Utils.getPlayer(uuid);
	}

	public void increaseStreak() {
		earnedToday = true;
		++streak;
		if (!getPlayer().isOnline()) return;
		new JsonBuilder()
				.next(StringUtils.getPrefix("DailyRewards") + "Your streak has &eincreased&3! " + "Use &c/dailyrewards &3to claim your reward")
				.command("/dr")
				.send(getPlayer().getPlayer());
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
