package gg.projecteden.nexus.features.events.y2025.pugmas25.quests;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.quests.CommonQuestItem;
import gg.projecteden.nexus.features.virtualinventories.events.VirtualInventoryConstructEvent;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.impl.VirtualPersonalBarrel;
import gg.projecteden.nexus.utils.Nullables;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

public class Pugmas25QuestItemsListener implements Listener {

	public Pugmas25QuestItemsListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(VirtualInventoryConstructEvent event) {
		if (!(event.getInventory() instanceof VirtualPersonalBarrel virtualPersonalBarrel))
			return;

		if (!Pugmas25.get().isAtEvent(virtualPersonalBarrel.getLocation()))
			return;

		virtualPersonalBarrel.getItems().add(CommonQuestItem.COIN_POUCH.get());
	}

	@EventHandler
	public void on(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		if (!Pugmas25.get().shouldHandle(player))
			return;

		if (event.getCause() != DamageCause.FALL)
			return;

		ItemStack boots = player.getInventory().getBoots();
		if (Nullables.isNullOrAir(boots) || !Pugmas25QuestItem.SHOCK_ABSORBENT_BOOTS.fuzzyMatch(boots))
			return;

		event.setCancelled(true);
		boots.damage(2, player);
	}
}
