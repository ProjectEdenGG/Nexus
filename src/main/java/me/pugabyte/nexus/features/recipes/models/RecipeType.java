package me.pugabyte.nexus.features.recipes.models;

import lombok.Getter;
import me.pugabyte.nexus.features.recipes.CustomRecipes;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public enum RecipeType {
	MAIN(null),
	SLABS(Material.OAK_SLAB),
	QUARTZ(Material.QUARTZ),
	BEDS(Material.CYAN_BED),
	DYES(Material.YELLOW_DYE),
	WOOL(Material.WHITE_WOOL),
	STONE_BRICK(Material.STONE_BRICKS),
	MISC(Material.BLUE_ICE),
	FUNCTIONAL(Material.CHEST);

	@Getter
	public ItemStack item;

	RecipeType(Material material) {
		if (material == null) return;
		this.item = new ItemBuilder(material).name("&e" + StringUtils.camelCase(name().replace("_", " "))).build();
	}

	public List<NexusRecipe> getRecipes() {
		return CustomRecipes.recipes.stream().filter(nexusRecipe -> nexusRecipe.getType() == this).collect(Collectors.toList());
	}

}
