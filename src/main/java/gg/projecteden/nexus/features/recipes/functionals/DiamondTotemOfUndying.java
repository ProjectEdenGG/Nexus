package gg.projecteden.nexus.features.recipes.functionals;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.resourcepack.CustomModel;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.utils.ItemUtils.isNullOrAir;

public class DiamondTotemOfUndying extends FunctionalRecipe {

	@Getter
	private static final ItemStack item = getCustomModel().getItem();

	public static CustomModel getCustomModel() {
		return CustomModel.of(Material.TOTEM_OF_UNDYING, 1);
	}

	@Override
	public ItemStack getResult() {
		return item;
	}

	@Override
	public Recipe getRecipe() {
		NamespacedKey key = new NamespacedKey(Nexus.getInstance(), "custom_diamond_totem_of_undying");
		return surroundRecipe(key, getResult(), Material.TOTEM_OF_UNDYING, Material.DIAMOND);
	}

	@Override
	public List<ItemStack> getIngredients() {
		return new ArrayList<>() {{
			add(new ItemStack(Material.DIAMOND));
			add(new ItemStack(Material.TOTEM_OF_UNDYING));
		}};
	}

	@Override
	public String[] getPattern() {
		return new String[] {"111", "121", "111"};
	}

	@Override
	public MaterialChoice getMaterialChoice() {
		return null;
	}

	@EventHandler
	public void onEntityResurrect(EntityResurrectEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		PlayerInventory inv = player.getInventory();
		if (inv.getItemInMainHand().getType().equals(Material.TOTEM_OF_UNDYING) || inv.getItemInOffHand().getType().equals(Material.TOTEM_OF_UNDYING))
			return;

		final ItemStack item = PlayerUtils.searchInventory(player, getCustomModel());
		if (isNullOrAir(item))
			return;

		event.setCancelled(false);
		// Delete the totem they have
		inv.removeItemAnySlot(item);
		// Move item from offhand
		ItemStack offHand = inv.getItemInOffHand().clone();
		Tasks.wait(1, () -> inv.setItemInOffHand(offHand));

		// Put a totem in their offhand
		inv.setItemInOffHand(item);
		Tasks.wait(2, player::updateInventory);
	}
}
