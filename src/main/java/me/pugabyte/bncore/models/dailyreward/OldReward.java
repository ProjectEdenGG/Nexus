package me.pugabyte.bncore.models.dailyreward;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
public class OldReward {
	private String description;
	private List<ItemStack> items;
	private Integer money;
	private Integer levels;
	private Integer votePoints;
	private String command;

	public OldReward(String description, ItemStack item) {
		this.description = description;
		this.items = Collections.singletonList(item);
	}

	public OldReward(String description, ItemStack... items) {
		this.description = description;
		this.items = Arrays.asList(items);
	}

	public OldReward(String description, List<ItemStack> items) {
		this.description = description;
		this.items = items;
	}

	public OldReward(String description, int money) {
		this.description = description;
		this.money = money;
	}

	public OldReward(String description, String command) {
		this.description = description;
		this.command = command;
	}
}
