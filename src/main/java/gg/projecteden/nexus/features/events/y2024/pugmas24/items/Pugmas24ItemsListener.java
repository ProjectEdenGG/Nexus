package gg.projecteden.nexus.features.events.y2024.pugmas24.items;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class Pugmas24ItemsListener implements Listener {

	public Pugmas24ItemsListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		if (!Pugmas24.get().shouldHandle(player))
			return;


	}


}
