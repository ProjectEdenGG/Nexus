package gg.projecteden.nexus.features.events.y2025.pugmas25.quests;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class Pugmas25QuestItemsListener implements Listener {

	public Pugmas25QuestItemsListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		if (!Pugmas25.get().shouldHandle(player))
			return;

		if (event.getCause() != DamageCause.FALL)
			return;

		if (!Pugmas25QuestItem.RED_BALLOON.isInInventory(player))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void on(PlayerItemConsumeEvent event) {
		if (!Pugmas25.get().shouldHandle(event.getPlayer()))
			return;

		if (!Pugmas25QuestItem.HEART_CRYSTAL.fuzzyMatch(event.getItem()))
			return;

		Pugmas25.get().addMaxHealth(event.getPlayer(), 2.0); // TODO: CHECK IF THEY WILL HIT MAX HEALTH --> 40.0
		// TODO: HEART EFFECTS

		/*
			TODO: FIX ISSUES:
				 PLAYER HAS TO BE HUNGRY TO EAT THE HEART --> PLAYER INTERACT EVENT INSTEAD?

		 */
	}


}
