package me.pugabyte.bncore.features.tameables;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.tameables.models.TameablesAction;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static me.pugabyte.bncore.features.tameables.Tameables.PREFIX;

public class TameablesListener implements Listener {

	TameablesListener() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player)) return;
		Player player = (Player) event.getDamager();
		Entity entity = event.getEntity();
		EntityType entityType = entity.getType();
		String entityTypeString = entityType.toString().toLowerCase();
		if (!isTameable(entityType)) return;
		Tameable tameable = (Tameable) entity;

		Map<Player, TameablesAction> actions = Tameables.getPendingActions();
		if (actions.containsKey(player)) {
			event.setCancelled(true);

			TameablesAction action = actions.get(player);
			switch (action.getType()) {
				case TRANSFER:
					if (!isOwner(player, tameable)) return;
					OfflinePlayer transfer = action.getPlayer();
					tameable.setOwner(transfer);
					player.sendMessage(PREFIX + "You have transferred the ownership of your " + entityTypeString + " to " + transfer.getName());
					break;
				case UNTAME:
					if (!isOwner(player, tameable)) return;
					tameable.setOwner(null);
					player.sendMessage(PREFIX + "You have untamed your " + entityTypeString);
					break;
				case INFO:
					if (tameable.isTamed()) {
						player.sendMessage(PREFIX + tameable.getOwner().getName() + " owns that " + entityTypeString);
					} else {
						player.sendMessage(PREFIX + "That " + entityTypeString + " is not tamed");
					}
					break;
			}
			Tameables.removePendingAction(player);
		}
	}

	private boolean isTameable(EntityType entityType) {
		List<EntityType> tameables = new ArrayList<>();
		tameables.add(EntityType.OCELOT);
		tameables.add(EntityType.WOLF);
		tameables.add(EntityType.PARROT);

		return tameables.contains(entityType);
	}

	private boolean isOwner(Player player, Tameable tameable) {
		boolean owner = tameable.getOwner().getUniqueId().toString().equals(player.getUniqueId().toString());
		if (!owner)
			player.sendMessage(PREFIX + "You do not own that animal!");
		return owner;
	}

}

