package me.pugabyte.bncore.models.dailyrewards;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

@Data
public class DailyReward {
	private String description;
	private List<ItemStack> items;
	private Integer money;

	public DailyReward(String description, ItemStack item) {
		this.description = description;
		this.items = Collections.singletonList(item);
	}

	public DailyReward(String description, List<ItemStack> items) {
		this.description = description;
		this.items = items;
	}

	public DailyReward(String description, int money) {
		this.description = description;
		this.money = money;
	}
}
