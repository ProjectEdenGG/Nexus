package gg.projecteden.nexus.features.recipes.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class RecipeGroup {
	@EqualsAndHashCode.Include
	final int id;
	final String displayName;
	final ItemStack displayItem;

}
