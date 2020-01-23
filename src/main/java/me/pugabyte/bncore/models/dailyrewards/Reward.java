package me.pugabyte.bncore.models.dailyrewards;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
public class Reward {
	private String description;
	private List<ItemStack> items;
	private Integer money;
	private String command;

	public Reward(String description, ItemStack item) {
		this.description = description;
		this.items = Collections.singletonList(item);
	}

	public Reward(String description, ItemStack... items) {
		this.description = description;
		this.items = Arrays.asList(items);
	}

	public Reward(String description, List<ItemStack> items) {
		this.description = description;
		this.items = items;
	}

	public Reward(String description, int money) {
		this.description = description;
		this.money = money;
	}

	public Reward(String description, String command) {
		this.description = description;
		this.command = command;
	}
}
