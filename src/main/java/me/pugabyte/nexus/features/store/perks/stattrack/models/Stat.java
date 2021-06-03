package me.pugabyte.nexus.features.store.perks.stattrack.models;

import lombok.Getter;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.Tool;
import me.pugabyte.nexus.utils.Tool.ToolGroup;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
	FLOWERS_PICKED(Tool.SHEARS, MaterialTag.ALL_FLOWERS);

	@Getter
	private List<Tool> tools = new ArrayList<>();
	@Getter
	private List<Material> materials = new ArrayList<>();

	Stat() {}

	Stat(Tool... tools) {
		this.tools = Arrays.asList(tools);
	}

	Stat(ToolGroup toolGroup) {
		this.tools = toolGroup.getTools();
	}

	Stat(Tool tool, Material... materials) {
		this.tools = Collections.singletonList(tool);
		this.materials = Arrays.asList(materials);
	}

	Stat(Tool tool, MaterialTag materials) {
		this.tools = Collections.singletonList(tool);
		this.materials = new ArrayList<>(materials.getValues());
	}

	public boolean isToolApplicable(Material material) {
		for (Tool tool : tools)
			if (tool.getTools().contains(material))
				return true;
		return false;
	}

	public String toString() {
		return (name().charAt(0) + name().substring(1).toLowerCase()).replaceAll("_", " ");
	}

	public static Stat of(String stat) {
		return valueOf(stat.replaceAll(" ", "_").toUpperCase());
	}
}
