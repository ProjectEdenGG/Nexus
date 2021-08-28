package gg.projecteden.nexus.features.recipes.functionals.armor.wither;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.features.wither.WitherChallenge;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;

public class CraftedWitherSkull extends FunctionalRecipe {

	@Getter
	public static final ItemStack item = new ItemBuilder(Material.WITHER_SKELETON_SKULL)
		.name("&bCrafted Wither Skeleton Skull")
		.lore("&7Can be used to craft Wither Armor")
		.build();

	@Override
	public ItemStack getResult() {
		return CraftedWitherSkull.getItem();
	}

	@Override
	public Recipe getRecipe() {
		NamespacedKey key = new NamespacedKey(Nexus.getInstance(), stripColor("custom_crafted_wither_skull"));
		ShapelessRecipe recipe = new ShapelessRecipe(key, item);
		recipe.addIngredient(4, WitherChallenge.getWitherFragment());
		return recipe;
	}

	@Override
	public List<ItemStack> getIngredients() {
		return new ArrayList<>() {{
			for (int i = 0; i < 4; i++)
				add(WitherChallenge.getWitherFragment());
		}};
	}

	@Override
	public String[] getPattern() {
		return null;
	}

	@Override
	public RecipeChoice.MaterialChoice getMaterialChoice() {
		return null;
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!ItemUtils.isFuzzyMatch(event.getItemInHand(), getItem())) return;
		if (event.getBlockAgainst().getType() == Material.SOUL_SAND) return;
		event.setCancelled(true);
	}

}
