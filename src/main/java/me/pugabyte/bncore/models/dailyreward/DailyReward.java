package me.pugabyte.bncore.models.dailyreward;

import com.dieselpoint.norm.serialize.DbSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.features.dailyrewards.DailyRewardsFeature;
import me.pugabyte.bncore.framework.persistence.serializer.mysql.IntegerListSerializer;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

	public void claim(int day) {
		claim(day, true);
	}

	public void claim(int day, boolean applyReward) {
		if (applyReward) applyReward(day);
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

	private void applyReward(int day) {
		Player player = (Player) getPlayer();

		Reward reward = DailyRewardsFeature.getReward(day);
		List<ItemStack> items = reward.getItems();
		Integer money = reward.getMoney();
		String command = reward.getCommand();

		if (items != null) {
			Utils.giveItems(player, items);
		}

		if (money != null) {
			// TODO: Hook into vault
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + player.getName() + " " + money.toString());
		}

		if (command != null && !command.isEmpty()) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("%player%", player.getName()));
		}
	}

}
