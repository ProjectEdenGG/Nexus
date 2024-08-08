package gg.projecteden.nexus.features.events.y2024.pugmas24.quests;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Pugmas24QuestItemsListener implements Listener {

	public Pugmas24QuestItemsListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		if (!Pugmas24.get().shouldHandle(player))
			return;

		if (event.getCause() != DamageCause.FALL)
			return;

		if (!Pugmas24QuestItem.RED_BALLOON.isInInventory(player))
			return;

		event.setCancelled(true);
	}


}
