package me.pugabyte.bncore.features.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.With;
import me.pugabyte.bncore.features.commands.TameablesCommand.TameablesAction.TameablesActionType;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TameablesCommand extends CustomCommand implements Listener {
	private static Map<Player, TameablesAction> actions = new HashMap<>();

	TameablesCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		send("Correct usage: &c/tameables <info|untame|transfer [player]>");
	}

	@Path("(info|view)")
	void info() {
		actions.put(player(), new TameablesAction(TameablesActionType.INFO));
		send(PREFIX + "Punch the animal you wish to view information on");
	}

	@Path("untame")
	void untame() {
		actions.put(player(), new TameablesAction(TameablesActionType.UNTAME));
		send(PREFIX + "Punch the animal you wish to remove ownership of");
	}

	@Path("transfer <player>")
	void transfer(OfflinePlayer transfer) {
		if (player().equals(transfer))
			error("You can't transfer an animal to yourself");
		actions.put(player(), new TameablesAction(TameablesActionType.TRANSFER, transfer));
		send(PREFIX + "Punch the animal you wish to transfer to " + transfer.getName());
	}

	@Data
	@AllArgsConstructor
	@RequiredArgsConstructor
	public static class TameablesAction {
		@NonNull
		private TameablesActionType type;
		@With
		private OfflinePlayer player;

		public enum TameablesActionType {
			TRANSFER,
			UNTAME,
			INFO
		}
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
			actions.remove(player);
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
		boolean owner = tameable.getOwner().equals(player);
		if (!owner)
			player.sendMessage(PREFIX + "You do not own that animal!");
		return owner;
	}

}
