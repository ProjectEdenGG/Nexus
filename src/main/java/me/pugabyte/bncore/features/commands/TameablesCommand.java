package me.pugabyte.bncore.features.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.With;
import me.pugabyte.bncore.features.commands.TameablesCommand.TameablesAction.TameablesActionType;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;

@NoArgsConstructor
public class TameablesCommand extends CustomCommand implements Listener {
	private static Map<Player, TameablesAction> actions = new HashMap<>();

	TameablesCommand(CommandEvent event) {
		super(event);
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

	@Path("count <entityType>")
	void count(TameableEntity entityType) {
		List<Entity> entities = find(entityType);
		send(PREFIX + "Found &e" + entities.size() + " " + camelCase(entityType.name()) + " &3in loaded chunks belonging to you");
	}

	@Path("summon <entityType>")
	void summon(SummonableTameableEntity entityType) {
		List<Entity> entities = find(entityType);
		entities.forEach(entity -> entity.teleport(player()));
		send(PREFIX + "Summoned &e" + entities.size() + " " + camelCase(entityType.name()) + "s &3in loaded chunks to your location");
	}

	private List<Entity> find(TameableEntityList entityType) {
		List<Entity> entities = new ArrayList<>();
		Bukkit.getWorlds().forEach(world -> {
			if (WorldGroup.get(world) == WorldGroup.get(player()))
				world.getEntities().forEach(entity -> {
					if (entity.getType() == EntityType.valueOf(entityType.name())) {
						if (entity instanceof Tameable) {
							Tameable tameable = (Tameable) entity;
							AnimalTamer tamer = tameable.getOwner();
							if (tamer != null && tameable.getOwner().getUniqueId() == player().getUniqueId())
								entities.add(entity);
						} else if (entity instanceof Fox) {
							Fox fox = (Fox) entity;
							if (fox.getFirstTrustedPlayer() == player() || fox.getSecondTrustedPlayer() == player())
								entities.add(entity);
						}
					}
			});
		});

		if (entities.size() == 0)
			error("Could not find any " + camelCase(entityType.name()) + " in loaded chunks belonging to you");

		return entities;
	}

	private interface TameableEntityList {
		String name();
	}

	private enum SummonableTameableEntity implements TameableEntityList {
		WOLF,
		CAT,
		FOX,
		PARROT
	}

	private enum TameableEntity implements TameableEntityList {
		WOLF,
		CAT,
		FOX,
		PARROT,
		HORSE,
		SKELETON_HORSE,
		DONKEY,
		MULE,
		LLAMA
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
		if (!(event.getEntity() instanceof Tameable)) return;

		Player player = (Player) event.getDamager();
		Tameable tameable = (Tameable) event.getEntity();
		String entityName = camelCase(tameable.getType().name());

		if (actions.containsKey(player)) {
			event.setCancelled(true);

			TameablesAction action = actions.get(player);
			switch (action.getType()) {
				case TRANSFER:
					if (!isOwner(player, tameable)) return;
					OfflinePlayer transfer = action.getPlayer();
					tameable.setOwner(transfer);
					player.sendMessage(PREFIX + "You have transferred the ownership of your " + entityName + " to " + transfer.getName());
					break;
				case UNTAME:
					if (!isOwner(player, tameable)) return;
					tameable.setOwner(null);
					player.sendMessage(PREFIX + "You have untamed your " + entityName);
					break;
				case INFO:
					if (tameable.isTamed()) {
						player.sendMessage(PREFIX + tameable.getOwner().getName() + " owns that " + entityName);
					} else {
						player.sendMessage(PREFIX + "That " + entityName + " is not tamed");
					}
					break;
			}
			actions.remove(player);
		}
	}

	private boolean isOwner(Player player, Tameable tameable) {
		AnimalTamer tamer = tameable.getOwner();
		if (!(tamer != null && tamer.equals(player)))
			player.sendMessage(PREFIX + "You do not own that animal!");
		return true;
	}

}
