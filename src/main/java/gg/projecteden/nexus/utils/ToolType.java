package gg.projecteden.nexus.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Order of this enum is important, please do not change it arbitrarily
public enum ToolType {
	SWORD(
		Material.WOODEN_SWORD,
		Material.STONE_SWORD,
		Material.IRON_SWORD,
		Material.GOLDEN_SWORD,
		Material.DIAMOND_SWORD,
		Material.NETHERITE_SWORD
	),
	PICKAXE(
		BlockTags.MINEABLE_WITH_PICKAXE,
		Material.WOODEN_PICKAXE,
		Material.STONE_PICKAXE,
		Material.GOLDEN_PICKAXE,
		Material.IRON_PICKAXE,
		Material.DIAMOND_PICKAXE,
		Material.NETHERITE_PICKAXE
	),
	AXE(
		BlockTags.MINEABLE_WITH_AXE,
		Material.WOODEN_AXE,
		Material.STONE_AXE,
		Material.GOLDEN_AXE,
		Material.IRON_AXE,
		Material.DIAMOND_AXE,
		Material.NETHERITE_AXE
	),
	SHOVEL(
		BlockTags.MINEABLE_WITH_SHOVEL,
		Material.WOODEN_SHOVEL,
		Material.STONE_SHOVEL,
		Material.GOLDEN_SHOVEL,
		Material.IRON_SHOVEL,
		Material.DIAMOND_SHOVEL,
		Material.NETHERITE_SHOVEL
	),
	HOE(
		BlockTags.MINEABLE_WITH_HOE,
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
	private final TagKey<Block> preferredToolTag;
	@Getter
	private final List<Material> tools;

	ToolType(Material... tools) {
		this(null, tools);
	}

	ToolType(TagKey<Block> preferredToolTag, Material... tools) {
		this.preferredToolTag = preferredToolTag;
		this.tools = Arrays.asList(tools);
	}

	public static ToolType of(ItemStack item) {
		return of(item.getType());
	}

	public static ToolType of(Material material) {
		for (ToolType toolType : values())
			if (toolType.getTools().contains(material))
				return toolType;

		return null;
	}

	public List<Material> getTools(List<ToolGrade> grades) {
		return new ArrayList<>() {{
			for (Material tool : tools)
				if (grades.contains(ToolGrade.of(tool)))
					add(tool);
		}};
	}

	public enum ToolGroup {
		ARMOR(HELMET, CHESTPLATE, LEGGINGS, BOOTS),
		WEAPONS(SWORD, AXE, BOW, CROSSBOW, TRIDENT),
		TOOLS(PICKAXE, AXE, SHOVEL, HOE, SHEARS);

		@Getter
		private final List<ToolType> tools;

		ToolGroup(ToolType... tools) {
			this.tools = Arrays.asList(tools);
		}
	}

	@Getter
	@AllArgsConstructor
	public enum ToolGrade implements ComparableEnum {
		WOODEN(2),
		STONE(4),
		IRON(6),
		GOLDEN(12),
		DIAMOND(8),
		NETHERITE(9),
		;

		private final double baseDiggingSpeed;

		public static @Nullable ToolGrade of(ItemStack tool) {
			return of(tool.getType());
		}

		public static @Nullable ToolGrade of(Material tool) {
			for (ToolGrade grade : values())
				if (tool.name().startsWith(grade.name()))
					return grade;

			return null;
		}

		public List<ToolGrade> getEqualAndHigherToolGrades() {
			return new ArrayList<>() {{
				for (ToolGrade grade : values())
					if (grade.ordinal() >= ordinal())
						add(grade);
			}};
		}
	}
}
