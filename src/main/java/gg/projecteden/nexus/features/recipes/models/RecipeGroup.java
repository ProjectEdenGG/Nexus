package gg.projecteden.nexus.features.recipes.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class RecipeGroup {

	final int id;
	final String displayName;
	final ItemStack displayItem;

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (getClass() != obj.getClass())
			return false;
		return this.id == ((RecipeGroup) obj).id;
	}
}
