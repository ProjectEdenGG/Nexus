package me.pugabyte.nexus.features.autosort.features;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.autosort.AutoSortFeature;
import me.pugabyte.nexus.features.recipes.RecipeUtils;
import me.pugabyte.nexus.models.autosort.AutoSortUser;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static eden.utils.StringUtils.camelCase;
import static java.util.stream.Collectors.joining;

@NoArgsConstructor
public class AutoCraft implements Listener {

	@Getter
	private static final Map<Material, List<Material>> autoCraftable = new LinkedHashMap<>() {{
			put(Material.DIAMOND_BLOCK, List.of(Material.DIAMOND));
			put(Material.EMERALD_BLOCK, List.of(Material.EMERALD));
			put(Material.GOLD_BLOCK, List.of(Material.GOLD_INGOT));
			put(Material.IRON_BLOCK, List.of(Material.IRON_INGOT));
			put(Material.REDSTONE_BLOCK, List.of(Material.REDSTONE));
			put(Material.LAPIS_BLOCK, List.of(Material.LAPIS_LAZULI));
			put(Material.COAL_BLOCK, List.of(Material.COAL));
			put(Material.GOLD_INGOT, List.of(Material.GOLD_NUGGET));
			put(Material.IRON_INGOT, List.of(Material.IRON_NUGGET));
			put(Material.QUARTZ_BLOCK, List.of(Material.QUARTZ));
	}};

	@Getter
	private static final Map<Material, List<ItemStack>> ingredients = new HashMap<>() {{
		for (Material material : autoCraftable.keySet()) {
			List<Material> expectedMaterials = new ArrayList<>(autoCraftable.get(material));
			Collections.sort(expectedMaterials);

			for (List<ItemStack> ingredients : RecipeUtils.uncraft(new ItemStack(material))) {
				List<Material> recipeMaterials = ingredients.stream().map(ItemStack::getType).sorted().toList();

				if (!recipeMaterials.equals(expectedMaterials))
					continue;

				put(material, ingredients);
				break;
			}

			if (get(material) == null) {
				String ingredients = expectedMaterials.stream()
						.map(StringUtils::camelCase)
						.collect(joining(", "));

				Nexus.severe("Could not find crafting recipe for " + camelCase(material) + " made of " + ingredients);
				autoCraftable.remove(material);
			}
		}
	}};

	public static List<ItemStack> getIngredients(Material material) {
		return ingredients.get(material);
	}

	private static Material getAutoCraftResult(Material material) {
		for (Material result : autoCraftable.keySet())
			if (autoCraftable.get(result).contains(material))
				return result;
		return null;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPickupItem(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		AutoSortUser user = AutoSortUser.of(player);

		if (!user.hasFeatureEnabled(AutoSortFeature.AUTO_CRAFT))
			return;

		Material material = event.getItem().getItemStack().getType();
		Material result = getAutoCraftResult(material);
		if (result == null)
			return;

		Inventory inventory = player.getInventory();
		List<ItemStack> ingredients = getIngredients(result);

		Tasks.wait(0, () -> {
			loop: while (true) {
				for (ItemStack ingredient : ingredients)
					if (!inventory.containsAtLeast(ingredient, ingredient.getAmount())) {
						break loop;
					}

				for (ItemStack ingredient : ingredients)
					inventory.removeItem(ingredient);

				PlayerUtils.giveItem(player, new ItemStack(result));
			}
		});
	}

}
