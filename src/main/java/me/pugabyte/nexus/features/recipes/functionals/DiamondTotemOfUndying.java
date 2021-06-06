package me.pugabyte.nexus.features.recipes.functionals;

import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.recipes.models.FunctionalRecipe;
import me.pugabyte.nexus.features.resourcepack.CustomModel;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;

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
	public void onEntityDamage(EntityDamageEvent event) {
		if (!event.getEntityType().equals(EntityType.PLAYER))
			return;

		Player player = (Player) event.getEntity();
		if (player.getHealth() - event.getFinalDamage() > 0.0)
			return;

		if (event.getCause().equals(DamageCause.VOID))
			return;

		PlayerInventory inv = player.getInventory();
		if (inv.getItemInMainHand().getType().equals(Material.TOTEM_OF_UNDYING) || inv.getItemInOffHand().getType().equals(Material.TOTEM_OF_UNDYING))
			return;

		final ItemStack item = PlayerUtils.searchInventory(player, getCustomModel());
		if (isNullOrAir(item))
			return;

		// Delete the totem they have
		inv.removeItemAnySlot(item);
		// Move item from offhand
		ItemStack offHand = inv.getItemInOffHand();
		if (!isNullOrAir(offHand))
			Tasks.wait(1, () -> inv.setItemInOffHand(offHand));

		// Put a totem in their offhand
		inv.setItemInOffHand(item);
	}

	@EventHandler
	public void onEntityResurrect(EntityResurrectEvent event) {
		if (!event.getEntity().getType().equals(EntityType.PLAYER))
			return;

		Player player = (Player) event.getEntity();
		Tasks.wait(1, player::updateInventory);
	}
}
