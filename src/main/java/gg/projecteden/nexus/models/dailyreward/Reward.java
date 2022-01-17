package gg.projecteden.nexus.models.dailyreward;

import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class Reward {
	private String description;
	private List<ItemStack> items = new ArrayList<>();
	private Integer money;
	private Integer levels;
	private Integer votePoints;
	private String command;

	public Reward(String description) {
		this.description = description;
	}

	public Reward item(Material material) {
		return item(material, 1);
	}

	public Reward item(Material material, int amount) {
		return item(new ItemStack(material, amount));
	}

	public Reward item(MaterialTag materialTag) {
		return item(materialTag, 1);
	}

	public Reward item(MaterialTag materialTag, int amount) {
		materialTag.getValues().forEach(material -> item(new ItemStack(material, amount)));
		return this;
	}

	public Reward item(ItemBuilder builder) {
		return item(builder.build());
	}

	public Reward item(ItemStack... items) {
		this.items.addAll(Arrays.asList(items));
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
		NONE,
		COLOR(Material.WHITE_BED, Material.CYAN_SHULKER_BOX),
		NAME(Material.PLAYER_HEAD);

		private final List<Material> materials = new ArrayList<>();

		RequiredSubmenu(Material... materials) {
			this.materials.addAll(Arrays.asList(materials));
		}

		public boolean contains(Material material) {
			return materials.contains(material);
		}

	}

}


