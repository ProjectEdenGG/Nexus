package me.pugabyte.nexus.features.store.perks.stattrack.models;

import lombok.Getter;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.Tool;
import me.pugabyte.nexus.utils.Tool.ToolGroup;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;

public enum Stat {
	BLOCKS_BROKEN(ToolGroup.TOOLS),
	MOBS_KILLED(ToolGroup.WEAPONS),
	DAMAGE_DEALT(ToolGroup.WEAPONS),
	DURABILITY_MENDED,
	STONE_MINED(Tool.PICKAXE, MaterialTag.ALL_STONE),
	WOOD_CHOPPED(Tool.AXE, MaterialTag.ALL_WOOD),
	DIRT_EXCAVATED(Tool.SHOVEL, MaterialTag.ALL_DIRT),
	SAND_EXCAVATED(Tool.SHOVEL, MaterialTag.ALL_SAND),
	PATHS_CREATED(Tool.SHOVEL, Material.GRASS_PATH),
	DIRT_TILLED(Tool.HOE, Material.FARMLAND),
	FLOWERS_PICKED(Tool.SHEARS, MaterialTag.ALL_FLOWERS),
	FISH_CAUGHT(Tool.FISHING_ROD, MaterialTag.ITEMS_FISHES),
	TREASURE_FISHED(Tool.FISHING_ROD);

	@Getter
	private List<Tool> tools = new ArrayList<>();
	@Getter
	private List<Material> materials = new ArrayList<>();

	Stat() {}

	Stat(Tool tools) {
		this.tools = Arrays.asList(tools);
	}

	Stat(ToolGroup toolGroup) {
		this.tools = toolGroup.getTools();
	}

	Stat(Tool tool, Material... materials) {
		this.tools = Collections.singletonList(tool);
		this.materials = Arrays.asList(materials);
	}

	Stat(Tool tool, Tag<Material> materials) {
		this.tools = Collections.singletonList(tool);
		this.materials = new ArrayList<>(materials.getValues());
	}

	Stat(Tool tool, MaterialTag materials) {
		this.tools = Collections.singletonList(tool);
		this.materials = new ArrayList<>(materials.getValues());
	}

	@Contract("null -> false; !null -> _")
	public boolean isToolApplicable(ItemStack item) {
		if (isNullOrAir(item))
			return false;

		return isToolApplicable(item.getType());
	}

	public boolean isToolApplicable(Material material) {
		for (Tool tool : tools)
			if (tool.getTools().contains(material))
				return true;
		return false;
	}

	@Contract("null -> false; !null -> _")
	public boolean isMaterialApplicable(ItemStack item) {
		if (isNullOrAir(item))
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
