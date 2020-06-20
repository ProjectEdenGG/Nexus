package me.pugabyte.bncore.features.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.With;
import me.pugabyte.bncore.features.commands.TameablesCommand.PendingTameblesAction.PendingTameablesActionType;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.HideFromHelp;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleteIgnore;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.StringUtils;
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
import java.util.UUID;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

@NoArgsConstructor
public class TameablesCommand extends CustomCommand implements Listener {
	private static final Map<UUID, PendingTameblesAction> actions = new HashMap<>();
	private static final Map<UUID, Entity> moveQueue = new HashMap<>();
	private static final String PREFIX = StringUtils.getPrefix("Tameables");

	TameablesCommand(CommandEvent event) {
		super(event);
	}

	@Path("(info|view)")
	void info() {
		actions.put(uuid(), new PendingTameblesAction(PendingTameablesActionType.INFO));
		send(PREFIX + "Punch the animal you wish to view information on");
	}

	@Path("untame")
	void untame() {
		actions.put(uuid(), new PendingTameblesAction(PendingTameablesActionType.UNTAME));
		send(PREFIX + "Punch the animal you wish to remove ownership of");
	}

	@Path("move")
	void move() {
		actions.put(uuid(), new PendingTameblesAction(PendingTameablesActionType.MOVE));
		send(PREFIX + "Punch the animal you wish to move");
	}

	@HideFromHelp
	@TabCompleteIgnore
	@Path("move here")
	void moveHere() {
		if (!moveQueue.containsKey(uuid()))
			error("You do not have any animal pending teleport");
		Entity entity = moveQueue.remove(uuid());
		entity.teleport(player());
		send(PREFIX + "Summoned your " + camelCase(entity.getType()));
	}

	@Path("transfer <player>")
	void transfer(OfflinePlayer transfer) {
		if (player().equals(transfer))
			error("You can't transfer an animal to yourself");
		actions.put(uuid(), new PendingTameblesAction(PendingTameablesActionType.TRANSFER, transfer));
		send(PREFIX + "Punch the animal you wish to transfer to " + transfer.getName());
	}

	@Path("count <entityType>")
	void count(TameableEntity entityType) {
		List<Entity> entities = find(entityType);
		send(PREFIX + "Found &e" + entities.size() + " " + camelCase(entityType) + " &3in loaded chunks belonging to you");
	}

	@Path("summon <entityType>")
	void summon(SummonableTameableEntity entityType) {
		List<Entity> entities = find(entityType);
		entities.forEach(entity -> entity.teleport(player()));
		send(PREFIX + "Summoned &e" + entities.size() + " " + camelCase(entityType) + "s &3in loaded chunks to your location");
	}

	private List<Entity> find(TameableEntityList entityType) {
		List<Entity> entities = new ArrayList<>();
		Bukkit.getWorlds().forEach(world -> {
			if (WorldGroup.get(world) == WorldGroup.get(player()))
				world.getEntities().forEach(entity -> {
					if (TameableEntity.isTameable(entity.getType()) && isOwner(player(), entity))
						entities.add(entity);
			});
		});

		if (entities.isEmpty())
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
		PARROT;

		public static boolean isSummonable(EntityType entityType) {
			try {
				valueOf(entityType.name());
				return true;
			} catch (IllegalArgumentException ex) {
				return false;
			}
		}
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
		LLAMA;

		public static boolean isTameable(EntityType entityType) {
			try {
				valueOf(entityType.name());
				return true;
			} catch (IllegalArgumentException ex) {
				return false;
			}
		}
	}

	@Data
	@AllArgsConstructor
	@RequiredArgsConstructor
	public static class PendingTameblesAction {
		@NonNull
		private PendingTameablesActionType type;
		@With
		private OfflinePlayer player;

		public enum PendingTameablesActionType {
			TRANSFER,
			UNTAME,
			MOVE,
			INFO
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player)) return;

		Player player = (Player) event.getDamager();
		UUID uuid = player.getUniqueId();
		Entity entity = event.getEntity();
		String entityName = camelCase(entity.getType());

		if (actions.containsKey(uuid)) {
			event.setCancelled(true);
			if (!TameableEntity.isTameable(event.getEntityType())) {
				player.sendMessage(colorize(PREFIX + "&cThat animal is not tameable"));
				actions.remove(uuid);
				return;
			}

			PendingTameblesAction action = actions.get(uuid);
			switch (action.getType()) {
				case TRANSFER:
					checkOwner(player, entity);
					OfflinePlayer transfer = action.getPlayer();
					updateOwner(entity, player, transfer);
					player.sendMessage(colorize(PREFIX + "You have transferred the ownership of your " + entityName + " to " + transfer.getName()));
					break;
				case UNTAME:
					checkOwner(player, entity);
					updateOwner(entity, player, null);
					player.sendMessage(colorize(PREFIX + "You have untamed your " + entityName));
					break;
				case MOVE:
					checkOwner(player, entity);
					moveQueue.put(player.getUniqueId(), event.getEntity());
					player.sendMessage(json(PREFIX + "Click here to summon your animal when you are ready").command("/tameables move here").build());
					break;
				case INFO:
					String owner = getOwner(entity);
					if (!isNullOrEmpty(owner))
						player.sendMessage(colorize(PREFIX + "That " + entityName + " is owned by &e" + owner));
					else
						player.sendMessage(colorize(PREFIX + "That " + entityName + " is not tamed"));
					break;
			}
			actions.remove(uuid);
		}
	}

	private void updateOwner(Entity entity, Player player, OfflinePlayer newOwner) {
		if (entity instanceof Tameable) {
			((Tameable) entity).setOwner(newOwner);
		} else if (entity instanceof Fox) {
			Fox fox = (Fox) entity;
			if (fox.getFirstTrustedPlayer() != null && fox.getFirstTrustedPlayer().getUniqueId().equals(player.getUniqueId()))
				fox.setFirstTrustedPlayer(newOwner);
			else if (fox.getSecondTrustedPlayer() != null && fox.getSecondTrustedPlayer().getUniqueId().equals(player.getUniqueId())) {
				fox.setSecondTrustedPlayer(newOwner);
			}
		}
	}

	private void checkOwner(Player player, Entity tameable) {
		if (!isOwner(player, tameable))
			error("You do not own that animal!");
	}

	private boolean isOwner(Player player, Entity entity) {
		if (entity instanceof Tameable) {
			AnimalTamer tamer = ((Tameable) entity).getOwner();
			return tamer != null && tamer.equals(player);
		} else if (entity instanceof Fox) {
			Fox fox = (Fox) entity;
			return fox.getFirstTrustedPlayer() == player() || fox.getSecondTrustedPlayer() == player();
		}
		return false;
	}

	private String getOwner(Entity entity) {
		if (entity instanceof Tameable) {
			AnimalTamer tamer = ((Tameable) entity).getOwner();
			if (tamer != null)
				return tamer.getName();
		} else if (entity instanceof Fox) {
			Fox fox = (Fox) entity;
			List<String> names = new ArrayList<>();
			if (fox.getFirstTrustedPlayer() != null)
				names.add(fox.getFirstTrustedPlayer().getName());
			if (fox.getSecondTrustedPlayer() != null)
				names.add(fox.getSecondTrustedPlayer().getName());
			if (!names.isEmpty())
				return String.join(" and ", names);
		}
		return null;
	}

}
