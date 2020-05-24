package me.pugabyte.bncore.models.dailyreward;

import lombok.Data;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

@Data
public class R3ward {
	private String description;
	private List<ItemStack> items;
	private Integer money;
	private Integer levels;
	private Integer votePoints;
	private String command;

	public R3ward(String description) {
		this.description = description;
	}

	public R3ward item(ItemStack... items) {
		this.items.addAll(Arrays.asList(items));
		return this;
	}

	public R3ward item(Material material) {
		return item(material, 1);
	}

	public R3ward item(Material material, int amount) {
		this.items.add(new ItemStack(material, amount));
		return this;
	}

	public R3ward item(MaterialTag materialTag) {
		return item(materialTag, 1);
	}

	public R3ward item(MaterialTag materialTag, int amount) {
		materialTag.getValues().forEach(material -> this.items.add(new ItemStack(material, amount)));
		return this;
	}

	public R3ward item(ItemBuilder builder) {
		this.items.add(builder.build());
		return this;
	}

	public R3ward command(String command) {
		this.command = command;
		return this;
	}

	public R3ward money(int money) {
		this.money = money;
		return this;
	}

	public R3ward levels(int levels) {
		this.levels = levels;
		return this;
	}

	public R3ward votePoints(int votePoints) {
		this.votePoints = votePoints;
		return this;
	}
}
