package me.pugabyte.bncore.features.store.perks.stattrack.models;

/* 1.13
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Stat {
	BLOCKS_BROKEN(
			Tool.PICKAXE,
			Tool.AXE,
			Tool.SHOVEL,
			Tool.SHEARS),
	MOBS_KILLED(
			Tool.BOW,
			Tool.SWORD),
	DAMAGE_DEALT(
			Tool.BOW,
			Tool.SWORD),
	DURABILITY_MENDED,
	STONE_MINED(Tool.PICKAXE,
			Material.STONE, Material.GRANITE, Material.POLISHED_GRANITE, Material.DIORITE, Material.POLISHED_DIORITE, Material.ANDESITE, Material.POLISHED_ANDESITE,
			Material.COBBLESTONE, Material.MOSSY_COBBLESTONE, Material.STONE_BRICKS, Material.MOSSY_STONE_BRICKS, Material.CRACKED_STONE_BRICKS, Material.CHISELED_STONE_BRICKS,
			Material.INFESTED_STONE_BRICKS, Material.INFESTED_MOSSY_STONE_BRICKS, Material.INFESTED_CRACKED_STONE_BRICKS, Material.INFESTED_CHISELED_STONE_BRICKS),
	WOOD_CHOPPED(Tool.AXE,
			Material.ACACIA_PLANKS, Material.BIRCH_PLANKS, Material.DARK_OAK_PLANKS, Material.JUNGLE_PLANKS, Material.OAK_PLANKS, Material.SPRUCE_PLANKS,
			Material.ACACIA_LOG, Material.BIRCH_LOG, Material.DARK_OAK_LOG, Material.JUNGLE_LOG, Material.OAK_LOG, Material.SPRUCE_LOG,
			Material.ACACIA_WOOD, Material.BIRCH_WOOD, Material.DARK_OAK_WOOD, Material.JUNGLE_WOOD, Material.OAK_WOOD, Material.SPRUCE_WOOD,
			Material.STRIPPED_ACACIA_LOG, Material.STRIPPED_BIRCH_LOG, Material.STRIPPED_DARK_OAK_LOG, Material.STRIPPED_JUNGLE_LOG, Material.STRIPPED_OAK_LOG, Material.STRIPPED_SPRUCE_LOG,
			Material.STRIPPED_ACACIA_WOOD, Material.STRIPPED_BIRCH_WOOD, Material.STRIPPED_DARK_OAK_WOOD, Material.STRIPPED_JUNGLE_WOOD, Material.STRIPPED_OAK_WOOD, Material.STRIPPED_SPRUCE_WOOD),
	FLOWERS_PICKED(Tool.SHEARS,
			Material.DANDELION, Material.ROSE_RED, Material.BLUE_ORCHID, Material.ALLIUM, Material.AZURE_BLUET,
			Material.RED_TULIP, Material.ORANGE_TULIP, Material.WHITE_TULIP, Material.PINK_TULIP, Material.OXEYE_DAISY,
			Material.SUNFLOWER, Material.LILAC, Material.ROSE_BUSH, Material.PEONY);


	private Tool tool = null;
	private List<Tool> tools = new ArrayList<>();
	private List<Material> materials = new ArrayList<>();

	Stat() {}

	Stat(Tool... tools) {
		this.tools = Arrays.asList(tools);
	}

	Stat(Tool tool, Material... materials) {
		this.tool = tool;
		this.materials = Arrays.asList(materials);
	}

	public Tool getTool() {
		return tool;
	}

	public List<Tool> getTools() {
		return tools;
	}

	public List<Material> getMaterials() {
		return materials;
	}

	public String toString() {
		return (name().charAt(0) + name().substring(1).toLowerCase()).replaceAll("_", " ");
	}
}
*/
