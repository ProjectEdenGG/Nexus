package gg.projecteden.nexus.hooks.libsdisguises;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.events.DisguiseEvent;
import me.libraryaddict.disguise.events.UndisguiseEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class LibsDisguisesHookImpl extends LibsDisguisesHook {

	@Override
	public boolean isDisguised(Entity entity) {
		return DisguiseAPI.isDisguised(entity);
	}

	@Override
	public void undisguiseToAll(Entity entity) {
		DisguiseAPI.undisguiseToAll(entity);
	}

	@Override
	public void setActionBarShown(Player player, boolean isShown) {
		DisguiseAPI.setActionBarShown(player, isShown);
	}

	@EventHandler
	public void on(DisguiseEvent event) {
		var hookEvent = new LibsDisguisesDisguiseEvent(event.getCommandSender(), event.getEntity());
		if (!hookEvent.callEvent())
			event.setCancelled(true);
	}

	@EventHandler
	public void on(UndisguiseEvent event) {
		var hookEvent = new LibsDisguisesUndisguiseEvent(event.getCommandSender(), event.getEntity(), event.isBeingReplaced());
		if (!hookEvent.callEvent())
			event.setCancelled(true);
	}
}
