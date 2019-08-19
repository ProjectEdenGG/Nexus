package me.pugabyte.bncore.models.dailyrewards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.omg.CORBA.INTERNAL;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static me.pugabyte.bncore.BNCore.log;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dailyrewards")
public class DailyRewards {
	private String uuid;
	private int streak;
	private boolean earnedToday = false;
	private String claimed;

	@Transient
	public OfflinePlayer getPlayer() {
		return BNCore.getPlayer(uuid);
	}

	public void increaseStreak() {
		++streak;
		if (getPlayer().getPlayer().hasPermission("rank.owner"))
			getPlayer().getPlayer().sendMessage(BNCore.getPrefix("DailyRewards") + "Your streak has &eincreased&3! " +
				"Use &c/dailyrewards &3to claim your reward");
	}

	public boolean hasClaimed(int day) {
		return deserializeClaimed().contains(day);
	}

	public void claim(int day) {
		claim(day, true);
	}

	public void claim(int day, boolean applyReward) {
		List<Integer> claimed = deserializeClaimed();
		claimed.add(day);
		this.claimed = serializeClaimed(claimed);

		if (applyReward) applyReward(day);
	}

	public void unclaim(Integer day) {
		List<Integer> claimed = deserializeClaimed();
		claimed.remove(day);
		this.claimed = serializeClaimed(claimed);
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

	private String serializeClaimed(List<Integer> claimed) {
		Set<String> serialized = new HashSet<>();
		Collections.sort(claimed);
		for (int claim : claimed) {
			serialized.add(String.valueOf(claim));
		}
		return String.join(",", serialized);
	}

	private List<Integer> deserializeClaimed() {
		List<Integer> days = new ArrayList<>();
		if (claimed != null && !claimed.isEmpty())
			Arrays.asList(claimed.split(",")).forEach(string -> days.add(Integer.parseInt(string)));
		Collections.sort(days);
		return days;
	}

}
