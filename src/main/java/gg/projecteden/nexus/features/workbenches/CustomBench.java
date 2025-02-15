package gg.projecteden.nexus.features.workbenches;

import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent.InteractType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.WorkBench;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStation;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.utils.Nullables;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@NoArgsConstructor
public abstract class CustomBench extends Feature implements Listener {

	public abstract CustomBenchType getBenchType();

	public static @Nullable CustomBenchType getCustomBench(ItemStack item) {
		if (Nullables.isNullOrAir(item))
			return null;

		for (CustomBenchType customBenchType : CustomBenchType.values())
			if (ItemModelType.of(item) == customBenchType.getItemModelType())
				return customBenchType;

		return null;
	}

	@Getter
	@AllArgsConstructor
	public enum CustomBenchType {
		DYE_STATION("Dye Station", ItemModelType.DYE_STATION, DyeStation::open),
		ENCHANTED_BOOK_SPLITTER("Enchanted Book Splitter", ItemModelType.ENCHANTED_BOOK_SPLITTER, EnchantedBookSplitter::open),
		TOOL_MODIFICATION_TABLE("Tool Modification Table", ItemModelType.TOOL_MODIFICATION_TABLE, null), // TODO: BLAST
		;

		private final String name;
		private final ItemModelType itemModelType;
		private final Consumer<Player> interact;

		public void interact(Player player) {
			if (interact != null)
				interact.accept(player);
		}
	}

	public static void registerRecipes() {
		for (CustomBench customBench : Features.getInheritors(CustomBench.class)) {
			if (!(customBench instanceof ICraftableCustomBench craftableCustomBench))
				continue;

			if (customBench.isUnreleased())
				continue;

			var recipeType = craftableCustomBench.getRecipeType();
			var benchRecipe = craftableCustomBench.getBenchRecipe();
			if (benchRecipe != null)
				benchRecipe.register(recipeType);

			var additionRecipes = craftableCustomBench.getAdditionRecipes();
			if (additionRecipes != null && !additionRecipes.isEmpty()) {
				for (RecipeBuilder<?> recipe : additionRecipes) {
					recipe.register(recipeType);
				}
			}
		}
	}

	@EventHandler
	public void on(DecorationInteractEvent event) {
		if (!(event.getDecoration().getConfig() instanceof WorkBench))
			return;

		if (event.getInteractType() != InteractType.RIGHT_CLICK)
			return;

		ItemFrame itemFrame = event.getDecoration().getItemFrame();
		if (itemFrame == null || Nullables.isNullOrAir(itemFrame.getItem()))
			return;

		CustomBenchType customBenchType = getCustomBench(itemFrame.getItem());
		if (customBenchType == null)
			return;

		customBenchType.interact(event.getPlayer());
	}
}
