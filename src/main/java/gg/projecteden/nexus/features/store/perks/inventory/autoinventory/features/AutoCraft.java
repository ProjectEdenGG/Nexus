package gg.projecteden.nexus.features.store.perks.inventory.autoinventory.features;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.recipes.RecipeUtils;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.AutoInventory;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.AutoInventoryFeature;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUser;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
public class AutoCraft implements Listener {

	@Getter
	private static final Map<Material, Set<Material>> autoCraftable = new LinkedHashMap<>() {{
		put(Material.DIAMOND_BLOCK, Set.of(Material.DIAMOND));
		put(Material.EMERALD_BLOCK, Set.of(Material.EMERALD));
		put(Material.COPPER_BLOCK, Set.of(Material.COPPER_INGOT));
		put(Material.RAW_COPPER_BLOCK, Set.of(Material.RAW_COPPER));
		put(Material.GOLD_BLOCK, Set.of(Material.GOLD_INGOT));
		put(Material.RAW_GOLD_BLOCK, Set.of(Material.RAW_GOLD));
		put(Material.IRON_BLOCK, Set.of(Material.IRON_INGOT));
		put(Material.RAW_IRON_BLOCK, Set.of(Material.RAW_IRON));
		put(Material.REDSTONE_BLOCK, Set.of(Material.REDSTONE));
		put(Material.LAPIS_BLOCK, Set.of(Material.LAPIS_LAZULI));
		put(Material.COAL_BLOCK, Set.of(Material.COAL));
		put(Material.GOLD_INGOT, Set.of(Material.GOLD_NUGGET));
		put(Material.IRON_INGOT, Set.of(Material.IRON_NUGGET));
		put(Material.QUARTZ_BLOCK, Set.of(Material.QUARTZ));
		put(Material.GLOWSTONE, Set.of(Material.GLOWSTONE_DUST));
		put(Material.HAY_BLOCK, Set.of(Material.WHEAT));
		put(Material.MELON, Set.of(Material.MELON_SLICE));
		put(Material.PRISMARINE, Set.of(Material.PRISMARINE_SHARD));
		put(Material.PRISMARINE_BRICKS, Set.of(Material.PRISMARINE_SHARD));
		put(Material.CLAY, Set.of(Material.CLAY_BALL));
		put(Material.SNOW_BLOCK, Set.of(Material.SNOWBALL));
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
						.collect(Collectors.joining(", "));

				Nexus.severe("Could not find crafting recipe for " + StringUtils.camelCase(material) + " made of " + ingredients);
				autoCraftable.remove(material);
			}
		}
	}};

	public static List<ItemStack> getIngredients(Material material) {
		return ingredients.get(material);
	}

	private static Material getAutoCraftResult(Material material, AutoInventoryUser user) {
		for (Material result : autoCraftable.keySet())
			if (autoCraftable.get(result).contains(material) && !user.getAutoCraftExclude().contains(result))
				return result;
		return null;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void on(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		final AutoInventoryUser user = AutoInventoryUser.of(player);

		if (!user.hasFeatureEnabled(AutoInventoryFeature.AUTOCRAFT))
			return;

		final Material material = event.getItem().getItemStack().getType();
		final Material result = getAutoCraftResult(material, user);
		if (result == null)
			return;

		if (user.getAutoCraftExclude().contains(result))
			return;

		final Inventory inventory = player.getInventory();
		final List<ItemStack> ingredients = getIngredients(result);

		Tasks.sync(() -> {
			loop: while (true) {
				for (ItemStack ingredient : ingredients)
					if (!inventory.containsAtLeast(ingredient, ingredient.getAmount()))
						break loop;

				for (ItemStack ingredient : ingredients)
					inventory.removeItem(ingredient);

				PlayerUtils.giveItem(player, new ItemStack(result));
			}
		});
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void on(InventoryOpenEvent event) {
		if (!(event.getPlayer() instanceof Player player))
			return;

		final AutoInventoryUser user = AutoInventoryUser.of(player);

		if (!user.hasFeatureEnabled(AutoInventoryFeature.AUTOCRAFT))
			return;

		final Inventory inventory = event.getInventory();

		if (!AutoInventory.isSortableChestInventory(player, inventory, event.getView().getTitle()))
			return;

		for (ItemStack item : inventory.getContents()) {
			if (Nullables.isNullOrAir(item))
				continue;

			final Material material = item.getType();
			final Material result = getAutoCraftResult(material, user);
			if (result == null)
				continue;

			if (user.getAutoCraftExclude().contains(result))
				continue;

			final List<ItemStack> ingredients = getIngredients(result);

			Tasks.sync(() -> {
				loop: while (true) {
					for (ItemStack ingredient : ingredients)
						if (!inventory.containsAtLeast(ingredient, ingredient.getAmount()))
							break loop;

					if (!PlayerUtils.hasRoomFor(inventory, new ItemStack(result)))
						break;

					for (ItemStack ingredient : ingredients)
						inventory.removeItem(ingredient);

					inventory.addItem(new ItemStack(result));
				}
			});
		}
	}

}

