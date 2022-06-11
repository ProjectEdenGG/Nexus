package gg.projecteden.nexus.features.legacy.listeners;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import gg.projecteden.nexus.features.commands.staff.admin.KillEntityCommand;
import gg.projecteden.nexus.utils.WorldGroup;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Entities implements Listener {

	@EventHandler
	public void on(EntityAddToWorldEvent event) {
		if (!(event.getEntity() instanceof LivingEntity entity))
			return;

		if (WorldGroup.of(entity) != WorldGroup.LEGACY)
			return;

		if (entity instanceof Player)
			return;

		if (KillEntityCommand.canKill(entity))
			entity.remove();
		else
			entity.setAI(false);
	}

}
