package me.pugabyte.bncore.features.recipes;

import lombok.Getter;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum CraftingMenuType {
	MAIN(null, null),
	SLABS(new ArrayList<>(), Material.OAK_SLAB),
	QUARTZ(new ArrayList<>(), Material.QUARTZ),
	BEDS(new ArrayList<>(), Material.CYAN_BED),
	DYES(new ArrayList<>(), Material.YELLOW_DYE),
	WOOL(new ArrayList<>(), Material.WHITE_WOOL),
	STONE_BRICK(new ArrayList<>(), Material.STONE_BRICKS),
	MISC(new ArrayList<>(), Material.BLUE_ICE);

	public List<CraftingRecipeMenu.CraftingRecipe> list;
	public ItemStack item;

	CraftingMenuType(ArrayList<CraftingRecipeMenu.CraftingRecipe> list, Material material) {
		this.list = list;
		if (material == null) return;
		this.item = new ItemBuilder(material).name("&e" + StringUtils.camelCase(name().replace("_", " "))).build();
	}

}
