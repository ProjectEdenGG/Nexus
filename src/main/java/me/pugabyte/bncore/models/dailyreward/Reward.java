package me.pugabyte.bncore.models.dailyreward;

import lombok.Data;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class Reward {
	private String description;
	private List<ItemStack> items;
	private Integer money;
	private Integer levels;
	private Integer votePoints;
	private String command;

	public Reward(String description) {
		this.description = description;
	}

	public Reward item(ItemStack... items) {
		this.items.addAll(Arrays.asList(items));
		return this;
	}

	public Reward item(Material material) {
		return item(material, 1);
	}

	public Reward item(Material material, int amount) {
		this.items.add(new ItemStack(material, amount));
		return this;
	}

	public Reward item(MaterialTag materialTag) {
		return item(materialTag, 1);
	}

	public Reward item(MaterialTag materialTag, int amount) {
		materialTag.getValues().forEach(material -> this.items.add(new ItemStack(material, amount)));
		return this;
	}

	public Reward item(ItemBuilder builder) {
		this.items.add(builder.build());
		return this;
	}

	public Reward item(ItemStack item){
		this.items.add(item);
		return this;
	}

	public Reward command(String command) {
		this.command = command;
		return this;
	}

	public Reward money(int money) {
		this.money = money;
		return this;
	}

	public Reward levels(int levels) {
		this.levels = levels;
		return this;
	}

	public Reward votePoints(int votePoints) {
		this.votePoints = votePoints;
		return this;
	}

	public enum RequiredSubmenu {
		NONE(), COLOR(Material.WHITE_BED, Material.WHITE_SHULKER_BOX), NAME(Material.PLAYER_HEAD);

		List materials;

		RequiredSubmenu(Material... materials){
			this.materials = Arrays.asList(materials);
		}

		public boolean contains(Material material){
			if(materials!=null&&materials.contains(material))
				return true;
			return false;
		}

	}

}


