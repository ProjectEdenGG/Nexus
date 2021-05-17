package me.pugabyte.nexus.features.autosort.features;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.autosort.AutoSortFeature;
import me.pugabyte.nexus.models.autosort.AutoSortUser;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class AutoCraft implements Listener {
	public static Map<Material, ItemStack> autoCraftMaterials = new HashMap<>() {{
		put(Material.DIAMOND, new ItemStack(Material.DIAMOND_BLOCK, 9));
		put(Material.EMERALD, new ItemStack(Material.EMERALD_BLOCK, 9));
		put(Material.GOLD_INGOT, new ItemStack(Material.GOLD_BLOCK, 9));
		put(Material.IRON_INGOT, new ItemStack(Material.IRON_BLOCK, 9));
		put(Material.REDSTONE, new ItemStack(Material.REDSTONE_BLOCK, 9));
		put(Material.LAPIS_LAZULI, new ItemStack(Material.LAPIS_BLOCK, 9));
		put(Material.COAL, new ItemStack(Material.COAL_BLOCK, 9));
		put(Material.GOLD_NUGGET, new ItemStack(Material.GOLD_INGOT, 9));
		put(Material.IRON_NUGGET, new ItemStack(Material.IRON_INGOT, 9));
		put(Material.QUARTZ, new ItemStack(Material.QUARTZ_BLOCK, 4));
	}};

	public static boolean isAutoCraftMaterial(Material material) {
		return autoCraftMaterials.containsKey(material);
	}

	public static ItemStack getAutoCraftResult(Material material) {
		return autoCraftMaterials.get(material).clone();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPickupItem(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		AutoSortUser user = AutoSortUser.of(player);

		if (!user.isFeatureEnabled(AutoSortFeature.AUTO_CRAFT))
			return;

		Material material = event.getItem().getItemStack().getType();
		if (!AutoCraft.isAutoCraftMaterial(material))
			return;

		Inventory inventory = player.getInventory();
		ItemStack[] contents = inventory.getContents();
		double count = 0;
		for (ItemStack _stack : contents)
			if (_stack != null && _stack.isSimilar(new ItemStack(material)))
				count += _stack.getAmount();

		if (!(count > 0))
			return;

		ItemStack result = getAutoCraftResult(material);
		int replace = (int) (count / result.getAmount());
		if (replace == 0) return;
		ItemStack toRemove = new ItemStack(material, replace * result.getAmount());
		inventory.removeItem(toRemove);

		PlayerUtils.giveItem(player, new ItemStack(result.getType(), replace));
	}

}
