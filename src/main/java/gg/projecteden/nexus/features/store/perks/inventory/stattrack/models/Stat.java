package gg.projecteden.nexus.features.store.perks.inventory.stattrack.models;

import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.ToolType;
import gg.projecteden.nexus.utils.ToolType.ToolGroup;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Stat {
	BLOCKS_BROKEN(ToolGroup.TOOLS),
	MOBS_KILLED(ToolGroup.WEAPONS),
	DAMAGE_DEALT(ToolGroup.WEAPONS),
	DURABILITY_MENDED,
	STONE_MINED(ToolType.PICKAXE, MaterialTag.ALL_STONE),
	WOOD_CHOPPED(ToolType.AXE, MaterialTag.ALL_WOOD),
	DIRT_EXCAVATED(ToolType.SHOVEL, MaterialTag.ALL_DIRT),
	SAND_EXCAVATED(ToolType.SHOVEL, MaterialTag.ALL_SAND),
	PATHS_CREATED(ToolType.SHOVEL, Material.DIRT_PATH),
	DIRT_TILLED(ToolType.HOE, Material.FARMLAND),
	FLOWERS_PICKED(ToolType.SHEARS, MaterialTag.ALL_FLOWERS),
	FISH_CAUGHT(ToolType.FISHING_ROD, MaterialTag.ITEMS_FISHES),
	TREASURE_FISHED(ToolType.FISHING_ROD);

	@Getter
	private List<ToolType> toolTypes = new ArrayList<>();
	@Getter
	private List<Material> materials = new ArrayList<>();

	Stat() {}

	Stat(ToolType tools) {
		this.toolTypes = Arrays.asList(tools);
	}

	Stat(ToolGroup toolGroup) {
		this.toolTypes = toolGroup.getTools();
	}

	Stat(ToolType toolType, Material... materials) {
		this.toolTypes = Collections.singletonList(toolType);
		this.materials = Arrays.asList(materials);
	}

	Stat(ToolType toolType, Tag<Material> materials) {
		this.toolTypes = Collections.singletonList(toolType);
		this.materials = new ArrayList<>(materials.getValues());
	}

	Stat(ToolType toolType, MaterialTag materials) {
		this.toolTypes = Collections.singletonList(toolType);
		this.materials = new ArrayList<>(materials.getValues());
	}

	@Contract("null -> false; !null -> _")
	public boolean isToolApplicable(ItemStack item) {
		if (Nullables.isNullOrAir(item))
			return false;

		return isToolApplicable(item.getType());
	}

	public boolean isToolApplicable(Material material) {
		for (ToolType toolType : toolTypes)
			if (toolType.getTools().contains(material))
				return true;
		return false;
	}

	@Contract("null -> false; !null -> _")
	public boolean isMaterialApplicable(ItemStack item) {
		if (Nullables.isNullOrAir(item))
			return false;

		return isMaterialApplicable(item.getType());
	}

	public boolean isMaterialApplicable(Material material) {
		return materials.contains(material);
	}

	public String toString() {
		return (name().charAt(0) + name().substring(1).toLowerCase()).replaceAll("_", " ");
	}

	public static Stat of(String stat) {
		return valueOf(stat.replaceAll(" ", "_").toUpperCase());
	}
}
