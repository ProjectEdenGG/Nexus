package me.pugabyte.nexus.features.recipes.functionals;

import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.recipes.models.FunctionalRecipe;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

public class DiamondTotemOfDying extends FunctionalRecipe {

	@Getter
	private static final ItemStack diamondTotemOfUndying = new ItemBuilder(Material.TOTEM_OF_UNDYING)
			.name("&bDiamond Totem Of Undying")
			.lore("&7Activates from anywhere", "&7in your inventory")
			.customModelData(1)
			.build();

	@Override
	public ItemStack getResult() {
		return diamondTotemOfUndying;
	}

	@Override
	public Recipe getRecipe() {
		NamespacedKey key = new NamespacedKey(Nexus.getInstance(), stripColor("custom_diamond_totem_of_undying"));
		return surroundRecipe(key, getResult(), Material.TOTEM_OF_UNDYING, Material.DIAMOND);
	}

	@Override
	public List<ItemStack> getIngredients() {
		return new ArrayList<ItemStack>() {{
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


		PlayerInventory inv = player.getInventory();
		if (inv.getItemInMainHand().getType().equals(Material.TOTEM_OF_UNDYING) || inv.getItemInOffHand().getType().equals(Material.TOTEM_OF_UNDYING)) {
			return;
		}

		if (!PlayerUtils.playerHas(player, diamondTotemOfUndying)) {
			return;
		}

		// Delete the totem they have
		inv.removeItemAnySlot(diamondTotemOfUndying);
		// Move item from offhand
		ItemStack offHand = inv.getItemInOffHand();
		if (!ItemUtils.isNullOrAir(offHand)) {
			Tasks.wait(1, () -> inv.setItemInOffHand(offHand));
		}

		// Put a totem in their offhand
		inv.setItemInOffHand(diamondTotemOfUndying);
	}

	@EventHandler
	public void onEntityResurrect(EntityResurrectEvent event) {
		if (!event.getEntity().getType().equals(EntityType.PLAYER))
			return;

		Player player = (Player) event.getEntity();
		Tasks.wait(1, player::updateInventory);
	}
}
