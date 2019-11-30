package me.pugabyte.bncore.features.stattrack.models;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public enum Tool {
	BOW(Material.BOW),
	SHIELD(Material.SHIELD),
	SHEARS(Material.SHEARS),
	FISHING_ROD(Material.FISHING_ROD);
	/* 1.13
	SWORD(
			Material.WOODEN_SWORD,
			Material.STONE_SWORD,
			Material.IRON_SWORD,
			Material.GOLDEN_SWORD,
			Material.DIAMOND_SWORD),
	PICKAXE(
			Material.WOODEN_PICKAXE,
			Material.STONE_PICKAXE,
			Material.GOLDEN_PICKAXE,
			Material.IRON_PICKAXE,
			Material.DIAMOND_PICKAXE),
	AXE(
			Material.WOODEN_AXE,
			Material.STONE_AXE,
			Material.GOLDEN_AXE,
			Material.IRON_AXE,
			Material.DIAMOND_AXE),
	SHOVEL(
			Material.WOODEN_SHOVEL,
			Material.STONE_SHOVEL,
			Material.GOLDEN_SHOVEL,
			Material.IRON_SHOVEL,
			Material.DIAMOND_SHOVEL),
	HOE(
			Material.WOODEN_HOE,
			Material.STONE_HOE,
			Material.GOLDEN_HOE,
			Material.IRON_HOE,
			Material.DIAMOND_HOE),
	HELMET(
			Material.LEATHER_HELMET,
			Material.CHAINMAIL_HELMET,
			Material.IRON_HELMET,
			Material.GOLDEN_HELMET,
			Material.DIAMOND_HELMET),
	CHESTPLATE(
			Material.LEATHER_CHESTPLATE,
			Material.CHAINMAIL_CHESTPLATE,
			Material.IRON_CHESTPLATE,
			Material.GOLDEN_CHESTPLATE,
			Material.DIAMOND_CHESTPLATE),
	LEGGINGS(
			Material.LEATHER_LEGGINGS,
			Material.CHAINMAIL_LEGGINGS,
			Material.IRON_LEGGINGS,
			Material.GOLDEN_LEGGINGS,
			Material.DIAMOND_LEGGINGS),
	BOOTS(
			Material.LEATHER_BOOTS,
			Material.CHAINMAIL_BOOTS,
			Material.IRON_BOOTS,
			Material.GOLDEN_BOOTS,
			Material.DIAMOND_BOOTS);
	*/

	private final List<Material> materials;

	Tool(Material... materials) {
		this.materials = Arrays.asList(materials);
	}

	public List<Material> getTools() {
		return materials;
	}

	public boolean isArmour() {
		return name().equals("HELMET") || name().equals("CHESTPLATE") || name().equals("LEGGINGS") || name().equals("BOOTS");
	}

	public boolean isWeapon() {
		return name().equals("BOW") || name().equals("SWORD");
	}
}
