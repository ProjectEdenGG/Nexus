package gg.projecteden.nexus.features.listeners;

import de.tr7zw.nbtapi.NBTEntity;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import org.bukkit.Material;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Food implements Listener {

	private static final List<Material> STACKABLE_FOODS = List.of(Material.BEETROOT_SOUP, Material.MUSHROOM_STEW, Material.RABBIT_STEW);

	@EventHandler
	public void onEat(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();

		switch (event.getItem().getType()) {
			case GLOW_BERRIES -> player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.GLOWING).duration(TickTime.MINUTE.x(1.5)).build());
		}
	}

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof MushroomCow mooshroom))
			return;

		// Suspicious stew
		if (new NBTEntity(mooshroom).hasKey("EffectId"))
			return;

		final Player player = event.getPlayer();
		final ItemStack item = player.getInventory().getItem(event.getHand());
		if (Nullables.isNullOrAir(item))
			return;

		if (item.getType() != Material.BOWL)
			return;

		event.setCancelled(true);
		item.subtract();
		player.getInventory().addItem(new ItemBuilder(Material.MUSHROOM_STEW).maxStackSize(64).build());
	}

	@EventHandler
	public void on(PrepareItemCraftEvent event) {
		if (Nullables.isNullOrAir(event.getInventory().getResult()))
			return;

		for (Material food : STACKABLE_FOODS) {
			if (event.getInventory().getResult().getType() != food)
				continue;

			event.getInventory().setResult(new ItemBuilder(food)
				.maxStackSize(64)
				.amount(event.getInventory().getResult().getAmount())
				.build());
		}
	}

}
