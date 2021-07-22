package gg.projecteden.nexus.utils;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

// Order of this enum is important, please do not change it arbitrarily
public enum Tool {
	SWORD(
			Material.WOODEN_SWORD,
			Material.STONE_SWORD,
			Material.IRON_SWORD,
			Material.GOLDEN_SWORD,
			Material.DIAMOND_SWORD,
			Material.NETHERITE_SWORD
	),
	PICKAXE(
			Material.WOODEN_PICKAXE,
			Material.STONE_PICKAXE,
			Material.GOLDEN_PICKAXE,
			Material.IRON_PICKAXE,
			Material.DIAMOND_PICKAXE,
			Material.NETHERITE_PICKAXE
	),
	AXE(
			Material.WOODEN_AXE,
			Material.STONE_AXE,
			Material.GOLDEN_AXE,
			Material.IRON_AXE,
			Material.DIAMOND_AXE,
			Material.NETHERITE_AXE
	),
	SHOVEL(
			Material.WOODEN_SHOVEL,
			Material.STONE_SHOVEL,
			Material.GOLDEN_SHOVEL,
			Material.IRON_SHOVEL,
			Material.DIAMOND_SHOVEL,
			Material.NETHERITE_SHOVEL
	),
	HOE(
			Material.WOODEN_HOE,
			Material.STONE_HOE,
			Material.GOLDEN_HOE,
			Material.IRON_HOE,
			Material.DIAMOND_HOE,
			Material.NETHERITE_HOE
	),
	SHEARS(Material.SHEARS),
	BOW(Material.BOW),
	CROSSBOW(Material.CROSSBOW),
	TRIDENT(Material.TRIDENT),
	SHIELD(Material.SHIELD),
	FISHING_ROD(Material.FISHING_ROD),
	HELMET(
			Material.TURTLE_HELMET,
			Material.LEATHER_HELMET,
			Material.CHAINMAIL_HELMET,
			Material.IRON_HELMET,
			Material.GOLDEN_HELMET,
			Material.DIAMOND_HELMET,
			Material.NETHERITE_HELMET
	),
	CHESTPLATE(
			Material.LEATHER_CHESTPLATE,
			Material.CHAINMAIL_CHESTPLATE,
			Material.IRON_CHESTPLATE,
			Material.GOLDEN_CHESTPLATE,
			Material.DIAMOND_CHESTPLATE,
			Material.NETHERITE_CHESTPLATE
	),
	LEGGINGS(
			Material.LEATHER_LEGGINGS,
			Material.CHAINMAIL_LEGGINGS,
			Material.IRON_LEGGINGS,
			Material.GOLDEN_LEGGINGS,
			Material.DIAMOND_LEGGINGS,
			Material.NETHERITE_LEGGINGS
	),
	BOOTS(
			Material.LEATHER_BOOTS,
			Material.CHAINMAIL_BOOTS,
			Material.IRON_BOOTS,
			Material.GOLDEN_BOOTS,
			Material.DIAMOND_BOOTS,
			Material.NETHERITE_BOOTS
	);

	@Getter
	private final List<Material> tools;

	Tool(Material... tools) {
		this.tools = Arrays.asList(tools);
	}

	public static Tool of(ItemStack item) {
		return of(item.getType());
	}

	public static Tool of(Material material) {
		for (Tool tool : values())
			if (tool.getTools().contains(material))
				return tool;

		return null;
	}

	public enum ToolGroup {
		ARMOUR(HELMET, CHESTPLATE, LEGGINGS, BOOTS),
		WEAPONS(SWORD, AXE, BOW, CROSSBOW, TRIDENT),
		TOOLS(PICKAXE, AXE, SHOVEL, HOE, SHEARS);

		@Getter
		private final List<Tool> tools;

		ToolGroup(Tool... tools) {
			this.tools = Arrays.asList(tools);
		}
	}
}
