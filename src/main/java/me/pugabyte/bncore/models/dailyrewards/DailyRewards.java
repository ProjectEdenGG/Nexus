package me.pugabyte.bncore.models.dailyrewards;

import com.dieselpoint.norm.serialize.DbSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.Utils;
import me.pugabyte.bncore.framework.persistence.serializer.IntegerListSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static me.pugabyte.bncore.Utils.colorize;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyRewards {
	private String uuid;
	private int streak;
	private boolean earnedToday = false;
	@DbSerializer(IntegerListSerializer.class)
	private List<Integer> claimed = new ArrayList<>();

	public OfflinePlayer getPlayer() {
		return Utils.getPlayer(uuid);
	}

	public void increaseStreak() {
		++streak;
		String message = colorize("Your streak has &eincreased&3! Use &c/dailyrewards &3to claim your reward");
		getPlayer().getPlayer().sendMessage(Utils.getPrefix("DailyRewards") + message);
	}

	public boolean hasClaimed(int day) {
		return claimed != null && claimed.contains(day);
	}

	public void claim(int day) {
		claim(day, true);
	}

	public void claim(int day, boolean applyReward) {
		if (applyReward) applyReward(day);
		claimed.add(day);
	}

	public void unclaim(Integer day) {
		claimed.remove(day);
	}

	private void applyReward(int day) {
		Player player = (Player) getPlayer();

		DailyReward dailyReward = BNCore.dailyRewards.getDailyReward(day);
		List<ItemStack> items = dailyReward.getItems();
		Integer money = dailyReward.getMoney();

		if (items != null) {
			for (ItemStack reward : items) {

				Map<Integer, ItemStack> excess = player.getInventory().addItem(reward);
				if (!excess.isEmpty()) {
					excess.values().forEach(itemStack -> player.getWorld().dropItemNaturally(player.getLocation(), itemStack));
				}
			}
		}

		if (money != null) {
			// TODO: Hook into vault
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + player.getName() + " " + money.toString());
		}
	}

}
