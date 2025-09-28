package gg.projecteden.nexus.features.recipes.functionals.backpacks;

import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks.BackpackTier;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class NetheriteBackpack extends DiamondBackpack {

	public static ItemStack result = BackpackTier.NETHERITE.builder()
		.name("Netherite Backpack")
		.build();

	@Override
	public ItemStack getItem() {
		return result;
	}

	@Override
	public ItemStack getResult() {
		return result;
	}

	@Override
	public ItemStack getPreviousBackpack() {
		return DiamondBackpack.result;
	}

	@Override
	public Material getUpgradeMaterial() {
		return Material.NETHERITE_SCRAP;
	}

	@Override
	public BackpackTier getTier() {
		return BackpackTier.NETHERITE;
	}

	@EventHandler
	public void onItemBurn(EntityDamageEvent event) {
		if (event.getEntity().getType() != EntityType.ITEM) return;

		ItemStack item = ((Item) event.getEntity()).getItemStack();
		if (!Backpacks.isBackpack(item)) return;
		if (BackpackTier.of(item) != BackpackTier.NETHERITE) return;

		if (Arrays.asList(EntityDamageEvent.DamageCause.LAVA, EntityDamageEvent.DamageCause.FIRE, EntityDamageEvent.DamageCause.FIRE_TICK).contains(event.getCause()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onItemBurn(EntityDamageByBlockEvent event) {
		if (event.getEntity().getType() != EntityType.ITEM) return;

		ItemStack item = ((Item) event.getEntity()).getItemStack();
		if (!Backpacks.isBackpack(item)) return;
		if (BackpackTier.of(item) != BackpackTier.NETHERITE) return;

		if (event.getDamager() == null)
			return;

		if (event.getCause() == EntityDamageEvent.DamageCause.LAVA)
			event.setCancelled(true);
	}

}
