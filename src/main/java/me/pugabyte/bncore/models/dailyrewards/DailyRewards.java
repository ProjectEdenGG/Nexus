package me.pugabyte.bncore.models.dailyrewards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dailyrewards")
public class DailyRewards {
	private String uuid;
	private int streak;
	private boolean earnedToday = false;
	private String claimed;

	public boolean hasClaimed(int day) {
		return parseClaimed().contains(String.valueOf(day));
	}

	public void claim(int day) {
		claim(day, true);
	}

	public void claim(int day, boolean applyReward) {
		List<String> claimed = parseClaimed();
		claimed.add(String.valueOf(day));
		this.claimed = String.join(",", claimed);

		if (applyReward) applyReward(day);
	}

	public void applyReward(int day) {
		Player player = Bukkit.getPlayer(UUID.fromString(uuid));

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

	private List<String> parseClaimed() {
		List<String> days = new ArrayList<>();
		if (claimed != null)
			Arrays.asList(claimed.split(",")).forEach(string -> days.add(String.valueOf(Integer.parseInt(string))));
		return days;
	}
}
