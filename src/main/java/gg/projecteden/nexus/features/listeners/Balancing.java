package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.utils.RandomUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class Balancing implements Listener {

	@EventHandler
	public void onEnderDragonDeath(EntityDeathEvent event) {
		if (!event.getEntityType().equals(EntityType.ENDER_DRAGON))
			return;

		if (RandomUtils.chanceOf(33))
			event.getDrops().add(new ItemStack(Material.DRAGON_EGG));
	}

	@EventHandler
	public void onDropTotem(EntityDeathEvent event) {
		if (!event.getEntityType().equals(EntityType.EVOKER))
			return;

		if (RandomUtils.chanceOf(50))
			event.getDrops().removeIf(item -> item.getType().equals(Material.TOTEM_OF_UNDYING));
	}
}
